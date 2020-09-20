package seedu.address.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.item.Item;

/**
 * An Immutable AddressBook that is serializable to JSON format.
 */
@JsonRootName(value = "addressbook")
class JsonSerializableAddressBook {

    public static final String MESSAGE_DUPLICATE_ITEM = "Items list contains duplicate item(s).";

    private final List<JsonAdaptedItem> items = new ArrayList<>();

    /**
     * Constructs a {@code JsonSerializableAddressBook} with the given items.
     */
    @JsonCreator
    public JsonSerializableAddressBook(@JsonProperty("items") List<JsonAdaptedItem> items) {
        this.items.addAll(items);
    }

    /**
     * Converts a given {@code ReadOnlyAddressBook} into this class for Jackson use.
     *
     * @param source future changes to this will not affect the created {@code JsonSerializableAddressBook}.
     */
    public JsonSerializableAddressBook(ReadOnlyAddressBook source) {
        items.addAll(source.getItemList().stream().map(JsonAdaptedItem::new).collect(Collectors.toList()));
    }

    /**
     * Converts this address book into the model's {@code AddressBook} object.
     *
     * @throws IllegalValueException if there were any data constraints violated.
     */
    public AddressBook toModelType() throws IllegalValueException {
        AddressBook addressBook = new AddressBook();
        for (JsonAdaptedItem jsonAdaptedItem : items) {
            Item item = jsonAdaptedItem.toModelType();
            if (addressBook.hasItem(item)) {
                throw new IllegalValueException(MESSAGE_DUPLICATE_ITEM);
            }
            addressBook.addItem(item);
        }
        return addressBook;
    }

}
