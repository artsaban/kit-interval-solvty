package ru.nsc.knime;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentStringSelection;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

/**
 * <code>NodeDialog</code> for the "IntervalSolvty" Node.
 * 
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more 
 * complex dialog please derive directly from 
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author Artem Shabanov
 */
public class IntervalSolvtyNodeDialog extends DefaultNodeSettingsPane {

    /**
     * New pane for configuring IntervalSolvty node dialog.
     * This is just a suggestion to demonstrate possible default dialog
     * components.
     */
    protected IntervalSolvtyNodeDialog() {
        super();
                
        addDialogComponent(
    		new DialogComponentStringSelection(
				new SettingsModelString(
						IntervalSolvtyNodeModel.FUNCTIONAL_KEY,
						IntervalSolvtyNodeModel.FUNCTIONAL_DEFAULT_VALUE
				),
				"Functional:",
				new String[] {"Tol", "Uni", "Uss", "Uns"}
			)
		);
    }
}

