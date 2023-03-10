package seedu.vms.ui;

import java.util.Collection;
import java.util.Comparator;
import java.util.function.Function;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;


/** An extension of {@link ListView} to display the values of a map. */
public class ListViewPanel<T extends Comparable<T>> extends ListView<T> {
    private final Function<T, Node> displayFunction;
    private final ObjectProperty<Comparator<T>> comparatorProperty =
            new SimpleObjectProperty<>(Comparator.naturalOrder());


    /**
     * Constructs a {@code ListViewPanel}.
     *
     * @param dataMap - the map of data to display within this list view.
     * @param displayFunction - factory function to generate the graphical
     *      representation of the data.
     * @param <T> - the type of data being displayed.
     */
    public ListViewPanel(ObservableMap<?, T> dataMap, Function<T, Node> displayFunction) {
        this.displayFunction = displayFunction;
        setCellFactory(listView -> new DisplayCell());
        dataMap.addListener(this::handleChange);
        updateList(dataMap.values());
    }


    /**
     * Returns the comparator property of this list view panel.
     */
    public ObjectProperty<Comparator<T>> comparatorProperty() {
        return comparatorProperty;
    }


    private void handleChange(MapChangeListener.Change<?, ? extends T> change) {
        updateList(change.getMap().values());
    }


    private void updateList(Collection<? extends T> updatedDatas) {
        getItems().setAll(updatedDatas);
        getItems().sort(comparatorProperty.get());
    }





    private class DisplayCell extends ListCell<T> {
        @Override
        protected void updateItem(T data, boolean empty) {
            super.updateItem(data, empty);
            if (empty || data == null) {
                setText(null);
                setGraphic(null);
            } else {
                setGraphic(displayFunction.apply(data));
            }
        }
    }
}
