package ru.nsc.knime;

import org.knime.core.node.NodeView;

/**
 * <code>NodeView</code> for the "IntervalSolvty" Node.
 * 
 *
 * @author Artem Shabanov
 */
public class IntervalSolvtyNodeView extends NodeView<IntervalSolvtyNodeModel> {

    /**
     * Creates a new view.
     * 
     * @param nodeModel The model (class: {@link IntervalSolvtyNodeModel})
     */
    protected IntervalSolvtyNodeView(final IntervalSolvtyNodeModel nodeModel) {
        super(nodeModel);

        // TODO instantiate the components of the view here.

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void modelChanged() {

        // TODO retrieve the new model from your nodemodel and 
        // update the view.
        IntervalSolvtyNodeModel nodeModel = 
            (IntervalSolvtyNodeModel)getNodeModel();
        assert nodeModel != null;
        
        // be aware of a possibly not executed nodeModel! The data you retrieve
        // from your nodemodel could be null, emtpy, or invalid in any kind.
        
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onClose() {
    
        // TODO things to do when closing the view
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onOpen() {

        // TODO things to do when opening the view
    }

}

