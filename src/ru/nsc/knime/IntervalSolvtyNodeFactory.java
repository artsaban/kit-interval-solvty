package ru.nsc.knime;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "IntervalSolvty" Node.
 * 
 *
 * @author Artem Shabanov
 */
public class IntervalSolvtyNodeFactory 
        extends NodeFactory<IntervalSolvtyNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public IntervalSolvtyNodeModel createNodeModel() {
        return new IntervalSolvtyNodeModel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNrNodeViews() {
        return 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeView<IntervalSolvtyNodeModel> createNodeView(final int viewIndex,
            final IntervalSolvtyNodeModel nodeModel) {
        return new IntervalSolvtyNodeView(nodeModel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasDialog() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeDialogPane createNodeDialogPane() {
        return new IntervalSolvtyNodeDialog();
    }

}

