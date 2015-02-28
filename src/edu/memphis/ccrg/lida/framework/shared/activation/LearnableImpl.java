/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
/**
 * 
 */
package edu.memphis.ccrg.lida.framework.shared.activation;

import java.util.logging.Level;
import java.util.logging.Logger;

import edu.memphis.ccrg.lida.framework.factories.DefaultFactoryManager;
import edu.memphis.ccrg.lida.framework.factories.FactoryManager;
import edu.memphis.ccrg.lida.framework.factories.StrategyFactory;
import edu.memphis.ccrg.lida.framework.initialization.Initializable;
import edu.memphis.ccrg.lida.framework.strategies.DecayStrategy;
import edu.memphis.ccrg.lida.framework.strategies.DefaultTotalActivationStrategy;
import edu.memphis.ccrg.lida.framework.strategies.ExciteStrategy;
import edu.memphis.ccrg.lida.framework.strategies.TotalActivationStrategy;
import edu.memphis.ccrg.lida.framework.tasks.TaskManager;

/**
 * Default implementation of {@link Learnable}.
 * 
 * @author Javier Snaider
 * @author Ryan J. McCall
 */
public class LearnableImpl extends ActivatibleImpl implements Learnable {

    private static final Logger logger = Logger.getLogger(LearnableImpl.class
            .getCanonicalName());

    private static final FactoryManager factoryManager = DefaultFactoryManager.getInstance();
    private static final StrategyFactory strategyFactory = factoryManager
            .getFactory(StrategyFactory.class);

    private double baseLevelActivation;
    private double learnableRemovalThreshold;
    private double baseLevelIncentiveSalience;
    private ExciteStrategy baseLevelExciteStrategy;
    private DecayStrategy baseLevelDecayStrategy;
    private TotalActivationStrategy totalActivationStrategy;
    private static final String DEFAULT_TOTAL_ACTIVATION_TYPE = "defaultTotalActivationStrategy";

    /**
     * Constructs a new instance with default values.
     */
    public LearnableImpl() {
        super();
        baseLevelActivation = DEFAULT_BASE_LEVEL_ACTIVATION;
        learnableRemovalThreshold = DEFAULT_LEARNABLE_REMOVAL_THRESHOLD;
        baseLevelIncentiveSalience = DEFAULT_BASE_LEVEL_INCENTIVE_SALIENCE;
        baseLevelDecayStrategy = strategyFactory.getStrategy(DecayStrategy.class);
        baseLevelExciteStrategy = strategyFactory.getStrategy(ExciteStrategy.class);
        totalActivationStrategy = strategyFactory.getStrategy(TotalActivationStrategy.class);
    }

    /**
     * If this method is overridden, this init() must be called first! i.e.
     * super.init(); Will set parameters with the following names:<br/>
     * <br/>
     * 
     * <b>learnable.baseLevelActivation</b> initial base-level activation<br/>
     * <b>learnable.baseLevelRemovalThreshold</b> initial removal threshold<br/>
     * <b>learnable.baseLevelDecayStrategy</b> name of base-level decay strategy<br/>
     * <b>learnable.baseLevelExciteStrategy</b> name of base-level excite
     * strategy<br/>
     * <b>learnable.totalActivationStrategy</b> name of total activation
     * strategy<br/>
     * <br/>
     * If any parameter is not specified its default value will be used.
     * 
     * @see Initializable
     */
    @Override
    public void init() {
        super.init();

        baseLevelActivation = getParam("learnable.baseLevelActivation",
                DEFAULT_BASE_LEVEL_ACTIVATION);
        learnableRemovalThreshold = getParam("learnable.baseLevelRemovalThreshold",
                DEFAULT_LEARNABLE_REMOVAL_THRESHOLD);
        String decayName = getParam("learnable.baseLevelDecayStrategy", "defaultDecayStrategy");
        baseLevelDecayStrategy = strategyFactory.getStrategy(decayName, DecayStrategy.class);
        String exciteName = getParam("learnable.baseLevelExciteStrategy",
                "defaultExciteStrategy");
        baseLevelExciteStrategy = strategyFactory.getStrategy(exciteName, ExciteStrategy.class);
        String totalActivationName = getParam("learnable.totalActivationStrategy",
                DEFAULT_TOTAL_ACTIVATION_TYPE);
        totalActivationStrategy = strategyFactory.getStrategy(totalActivationName,
                TotalActivationStrategy.class);
        if (totalActivationStrategy == null) {
            totalActivationStrategy = strategyFactory.getStrategy(
                    DEFAULT_TOTAL_ACTIVATION_TYPE, TotalActivationStrategy.class);
        }
    }

    @Override
    public double getBaseLevelIncentiveSalience() {
        return baseLevelIncentiveSalience;
    }

    @Override
    public synchronized void setBaseLevelIncentiveSalience(double s) {
        if (s < 0.0) {
            synchronized (this) {
                baseLevelIncentiveSalience = 0.0;
            }
        } else if (s > 1.0) {
            synchronized (this) {
                baseLevelIncentiveSalience = 1.0;
            }
        } else {
            synchronized (this) {
                baseLevelIncentiveSalience = s;
            }
        }
    }

    @Override
    public double getTotalIncentiveSalience() {
        return totalActivationStrategy.calculateTotalActivation(
                getBaseLevelIncentiveSalience(), getIncentiveSalience());
    }

    @Override
    public void decay(long ticks) {
        decayBaseLevelActivation(ticks);
        decayBaseLevelIncentiveSalience(ticks);
        super.decay(ticks);
    }

    @Override
    public boolean isRemovable() {
        return (getBaseLevelActivation() + getBaseLevelIncentiveSalience()) <= learnableRemovalThreshold;
    }

    @Override
    public double getTotalActivation() {
        return totalActivationStrategy.calculateTotalActivation(getBaseLevelActivation(),
                getActivation());
    }

    @Override
    public void decayBaseLevelActivation(long ticks) {
        if (baseLevelDecayStrategy != null) {
            if (logger.isLoggable(Level.FINEST)) {
                logger.log(Level.FINEST, "Before decaying {1} has base-level activation: {2}",
                        new Object[] { TaskManager.getCurrentTick(), this,
                                getBaseLevelActivation() });
            }
            synchronized (this) {
                baseLevelActivation = baseLevelDecayStrategy.decay(getBaseLevelActivation(),
                        ticks);
            }
            if (logger.isLoggable(Level.FINEST)) {
                logger.log(Level.FINEST, "After decaying {1} has base-level activation: {2}",
                        new Object[] { TaskManager.getCurrentTick(), this,
                                getBaseLevelActivation() });
            }
        }
    }

    @Override
    public void decayBaseLevelIncentiveSalience(long t) {
        if (baseLevelDecayStrategy != null) {
            if (logger.isLoggable(Level.FINEST)) {
                logger.log(Level.FINEST,
                        "Before decaying {1} has base-level IncentiveSalience: {2}",
                        new Object[] { TaskManager.getCurrentTick(), this,
                                getBaseLevelIncentiveSalience() });
            }
            synchronized (this) {
                baseLevelIncentiveSalience = baseLevelDecayStrategy.decay(
                        getBaseLevelIncentiveSalience(), t);
            }
            if (logger.isLoggable(Level.FINEST)) {
                logger.log(Level.FINEST,
                        "After decaying {1} has base-level IncentiveSalience: {2}",
                        new Object[] { TaskManager.getCurrentTick(), this,
                                getBaseLevelIncentiveSalience() });
            }
        }
    }

    @Override
    public void reinforceBaseLevelActivation(double amount) {
        if (baseLevelExciteStrategy != null) {
            if (logger.isLoggable(Level.FINEST)) {
                logger.log(Level.FINEST,
                        "Before reinforcement {1} has base-level activation: {2}",
                        new Object[] { TaskManager.getCurrentTick(), this,
                                getBaseLevelActivation() });
            }
            synchronized (this) {
                baseLevelActivation = baseLevelExciteStrategy.excite(getBaseLevelActivation(),
                        amount);
            }
            if (logger.isLoggable(Level.FINEST)) {
                logger.log(Level.FINEST,
                        "After reinforcement {1} has base-level activation: {2}", new Object[] {
                                TaskManager.getCurrentTick(), this, getBaseLevelActivation() });
            }
        }
    }

    @Override
    public void reinforceBaseLevelIncentiveSalience(double amount) {
        if (baseLevelExciteStrategy != null) {
            if (logger.isLoggable(Level.FINEST)) {
                logger.log(Level.FINEST,
                        "Before reinforcement {1} has base-level IncentiveSalience: {2}",
                        new Object[] { TaskManager.getCurrentTick(), this,
                                getBaseLevelIncentiveSalience() });
            }
            synchronized (this) {
                baseLevelIncentiveSalience = baseLevelExciteStrategy.excite(
                        getBaseLevelIncentiveSalience(), amount);
            }
            if (logger.isLoggable(Level.FINEST)) {
                logger.log(Level.FINEST,
                        "After reinforcement {1} has base-level IncentiveSalience: {2}",
                        new Object[] { TaskManager.getCurrentTick(), this,
                                getBaseLevelIncentiveSalience() });
            }
        }
    }

    @Override
    public ExciteStrategy getBaseLevelExciteStrategy() {
        return baseLevelExciteStrategy;
    }

    @Override
    public void setBaseLevelExciteStrategy(ExciteStrategy s) {
        baseLevelExciteStrategy = s;
    }

    @Override
    public DecayStrategy getBaseLevelDecayStrategy() {
        return baseLevelDecayStrategy;
    }

    @Override
    public void setBaseLevelDecayStrategy(DecayStrategy s) {
        baseLevelDecayStrategy = s;
    }

    @Override
    public void setBaseLevelActivation(double a) {
        if (a < 0.0) {
            synchronized (this) {
                baseLevelActivation = 0.0;
            }
        } else if (a > 1.0) {
            synchronized (this) {
                baseLevelActivation = 1.0;
            }
        } else {
            synchronized (this) {
                baseLevelActivation = a;
            }
        }
    }

    @Override
    public double getBaseLevelActivation() {
        return baseLevelActivation;
    }

    @Override
    public double getBaseLevelRemovalThreshold() {
        return learnableRemovalThreshold;
    }

    @Override
    public void setBaseLevelRemovalThreshold(double t) {
        learnableRemovalThreshold = t;
    }

    @Override
    public TotalActivationStrategy getTotalActivationStrategy() {
        return totalActivationStrategy;
    }

    @Override
    public void setTotalActivationStrategy(TotalActivationStrategy s) {
        totalActivationStrategy = s;
    }
}
