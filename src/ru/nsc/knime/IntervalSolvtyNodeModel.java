package ru.nsc.knime;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.PriorityQueue;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DoubleValue;
import org.knime.core.data.RowKey;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

import net.java.jinterval.interval.set.SetInterval;
import net.java.jinterval.interval.set.SetIntervalContext;
import net.java.jinterval.interval.set.SetIntervalContexts;
import net.java.jinterval.rational.ExtendedRational;
import net.java.jinterval.rational.ExtendedRationalContext;
import net.java.jinterval.rational.ExtendedRationalContexts;
import optimization.ListItemGradient;
import ru.nsc.interval.solvty.IWithGradientEstimator;
import ru.nsc.interval.solvty.tol.TolSolvtyGradient;
import ru.nsc.interval.solvty.uni.UniSolvtyGradient;
import ru.nsc.interval.solvty.uns.UnsSolvtyGradient;
import ru.nsc.interval.solvty.uss.UssSolvtyGradient;

import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;


/**
 * This is the model implementation of IntervalSolvty.
 * 
 *
 * @author Artem Shabanov
 */
public class IntervalSolvtyNodeModel extends NodeModel {
    
    // the logger instance
    private static final NodeLogger logger = NodeLogger
            .getLogger(IntervalSolvtyNodeModel.class);
    
    // Settings keys
    
	static final String FUNCTIONAL_KEY = "FUNCTIONAL_KEY";
	    
    static final String VARIABLES_COUNT_KEY = "VARIABLES_COUNT";
    
    static final String EQUATIONS_COUNT_KEY = "EQUATIONS_COUNT";
    
    // Settings values
    
    static final String FUNCTIONAL_DEFAULT_VALUE = "Tol";
    
    static int VARIABLES_COUNT_VALUE;
    
    static int EQUATIONS_COUNT_VALUE;
    
    private final SettingsModelString funcValue =
    		new SettingsModelString(FUNCTIONAL_KEY, FUNCTIONAL_DEFAULT_VALUE);

    /**
     * Constructor for the node model.
     */
    protected IntervalSolvtyNodeModel() {
    
        // TODO one incoming port and one outgoing port is assumed
        super(2, 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected BufferedDataTable[] execute(final BufferedDataTable[] inData,
            final ExecutionContext exec) throws Exception {
        // TODO do something here
        logger.info("Node Model Stub... this is not yet implemented !");
        
        BufferedDataTable inTable = inData[0];
        BufferedDataTable initialBoxTable = inData[1];
        
        EQUATIONS_COUNT_VALUE = (int) inTable.size();
        VARIABLES_COUNT_VALUE = inTable.getDataTableSpec().getNumColumns() / 2 - 1;
        final int B_INF_INDEX =  2 * VARIABLES_COUNT_VALUE;
        
        SetIntervalContext ic = SetIntervalContexts.getAccur64();
        ExtendedRationalContext rc = ExtendedRationalContexts.exact();
        
        SetInterval[][] a = new SetInterval[EQUATIONS_COUNT_VALUE][VARIABLES_COUNT_VALUE];
        SetInterval[] b = new SetInterval[EQUATIONS_COUNT_VALUE];
        SetInterval[] x = new SetInterval[VARIABLES_COUNT_VALUE];
        ExtendedRational eps = ExtendedRational.valueOf(1.e-6);
         
        int rowNumber = 0;
        for (DataRow tableRow : inTable) {
        	for (int j = 0; j < VARIABLES_COUNT_VALUE; j++) {
        		DoubleValue inf = (DoubleValue) tableRow.getCell(2 * j);
        		DoubleValue sup = (DoubleValue) tableRow.getCell(2 * j + 1);
        		
				a[rowNumber][j] = ic.numsToInterval(inf.getDoubleValue(), sup.getDoubleValue());
			}
        	
        	DoubleValue inf = (DoubleValue) tableRow.getCell(B_INF_INDEX);
        	DoubleValue sup = (DoubleValue) tableRow.getCell(B_INF_INDEX + 1);
        	b[rowNumber] = ic.numsToInterval(inf.getDoubleValue(), sup.getDoubleValue());
        	
        	rowNumber++;
        }
        
        rowNumber = 0;
        for (DataRow tableRow : initialBoxTable) {
    		DoubleValue inf = (DoubleValue) tableRow.getCell(0);
    		DoubleValue sup = (DoubleValue) tableRow.getCell(1);
			x[rowNumber] = ic.numsToInterval(inf.getDoubleValue(), sup.getDoubleValue());
			rowNumber++;
        }
              
        IWithGradientEstimator func = new TolSolvtyGradient();
        if (funcValue.getStringValue() == "Uni") {
        	func = new UniSolvtyGradient();
        } else if (funcValue.getStringValue() == "Uss") {
        	func = new UssSolvtyGradient();
        } else if (funcValue.getStringValue() == "Uns") {
        	func = new UnsSolvtyGradient();
        }
        
        PriorityQueue<ListItemGradient> wList = func.getInstance().calc(x, a, b, eps, ic, rc);

        DataColumnSpec[] colSpecs = new DataColumnSpec[VARIABLES_COUNT_VALUE + 1];
        
        for (int i = 0; i < colSpecs.length - 1; i++) {
			colSpecs[i] = new DataColumnSpecCreator("x_" + i, DoubleCell.TYPE).createSpec();
		}
        
        colSpecs[colSpecs.length - 1] = new DataColumnSpecCreator("arg", DoubleCell.TYPE).createSpec();
        
        DataTableSpec outputSpec = new DataTableSpec(colSpecs);
        BufferedDataContainer container = exec.createDataContainer(outputSpec);
        
        DataCell[] infCells = new DataCell[colSpecs.length];
        DataCell[] supCells = new DataCell[colSpecs.length];
        
        for (int j = 0; j < infCells.length - 1; j++) {
			infCells[j] = new DoubleCell(wList.peek().getArgument()[j].doubleInf());
			supCells[j] = new DoubleCell(wList.peek().getArgument()[j].doubleSup());
		}
        
        infCells[infCells.length - 1] = new DoubleCell(wList.peek().getEstimation().doubleInf());
        supCells[infCells.length - 1] = new DoubleCell(wList.peek().getEstimation().doubleSup());
            
        DataRow row = new DefaultRow(new RowKey("inf"), infCells);
        container.addRowToTable(row);
        row = new DefaultRow(new RowKey("sup"), supCells);
        container.addRowToTable(row);
    
        container.close();
        BufferedDataTable out = container.getTable();
        return new BufferedDataTable[]{out};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void reset() {
        // TODO Code executed on reset.
        // Models build during execute are cleared here.
        // Also data handled in load/saveInternals will be erased here.
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DataTableSpec[] configure(final DataTableSpec[] inSpecs)
            throws InvalidSettingsException {

        return new DataTableSpec[]{null};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {

        // TODO save user settings to the config object.

    	funcValue.saveSettingsTo(settings);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
            throws InvalidSettingsException {
            
        // TODO load (valid) settings from the config object.
        // It can be safely assumed that the settings are valided by the 
        // method below.

    	funcValue.loadSettingsFrom(settings);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {
            
        // TODO check if the settings could be applied to our model
        // e.g. if the count is in a certain range (which is ensured by the
        // SettingsModel).
        // Do not actually set any values of any member variables.

    	funcValue.validateSettings(settings);
    	
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadInternals(final File internDir,
            final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {
        
        // TODO load internal data. 
        // Everything handed to output ports is loaded automatically (data
        // returned by the execute method, models loaded in loadModelContent,
        // and user settings set through loadSettingsFrom - is all taken care 
        // of). Load here only the other internals that need to be restored
        // (e.g. data used by the views).

    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveInternals(final File internDir,
            final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {
       
        // TODO save internal models. 
        // Everything written to output ports is saved automatically (data
        // returned by the execute method, models saved in the saveModelContent,
        // and user settings saved through saveSettingsTo - is all taken care 
        // of). Save here only the other internals that need to be preserved
        // (e.g. data used by the views).

    }

}

