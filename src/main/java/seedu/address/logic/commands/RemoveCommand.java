package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_QUANTITY;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_ITEMS;

import java.util.List;
import java.util.Set;

import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.commands.results.CommandResult;
import seedu.address.model.Model;
import seedu.address.model.item.Item;
import seedu.address.model.item.Metric;
import seedu.address.model.item.Name;
import seedu.address.model.item.Quantity;
import seedu.address.model.item.Supplier;
import seedu.address.model.tag.Tag;

/**
 * Removes a quantity from an existing item in the inventory book.
 */
public class RemoveCommand extends Command {

    public static final String COMMAND_WORD = "remove";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Removes quantity from the item identified "
            + "by the index number used in the displayed item list. "
            + "Existing quantity will be subtracted by the input values.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + "[" + PREFIX_QUANTITY + "QUANTITY] "
            + "Example: " + COMMAND_WORD + " 1 "
            + PREFIX_QUANTITY + "5 ";

    public static final String MESSAGE_EDIT_ITEM_SUCCESS = "Updated Item Quantity: %1$s";
    public static final String MESSAGE_NO_QUANTITY = "Quantity field must be provided.";
    public static final String MESSAGE_DUPLICATE_ITEM = "This item already exists in the inventory book.";

    private final Index index;
    private final Quantity quantity;
    /**
     * @param index of the item in the filtered item list to edit
     * @param quantity to be subtracted
     */
    public RemoveCommand(Index index, Quantity quantity) {
        requireNonNull(index);
        requireNonNull(quantity);

        this.index = index;
        this.quantity = quantity;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Item> lastShownList = model.getFilteredItemList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_ITEM_DISPLAYED_INDEX);
        }

        Item itemToEdit = lastShownList.get(index.getZeroBased());

        if (Integer.parseInt(itemToEdit.getQuantity().value) < Integer.parseInt(this.quantity.value)) {
            throw new CommandException(Quantity.MESSAGE_INVALID_QUANTITY_REMOVED);
        }

        Item editedItem = createRemovedItem(itemToEdit, quantity);

        if (!itemToEdit.isSameItem(editedItem) && model.hasItem(editedItem)) {
            throw new CommandException(MESSAGE_DUPLICATE_ITEM);
        }

        model.setItem(itemToEdit, editedItem);
        model.updateFilteredItemList(PREDICATE_SHOW_ALL_ITEMS);
        return new CommandResult(String.format(MESSAGE_EDIT_ITEM_SUCCESS, editedItem));
    }

    /**
     * Creates and returns a {@code Item} with the details of {@code itemToEdit}
     * after removing {@code quantity}.
     */
    private static Item createRemovedItem(Item itemToEdit, Quantity quantity) {
        assert itemToEdit != null;

        Name updatedName = itemToEdit.getName();
        Quantity updatedQuantity = itemToEdit.getQuantity().subtract(quantity);
        Supplier updatedSupplier = itemToEdit.getSupplier();
        Set<Tag> updatedTags = itemToEdit.getTags();
        Quantity updatedMaxQuantity = itemToEdit.getMaxQuantity().orElse(null);
        Metric metric = itemToEdit.getMetric().orElse(null);

        return new Item(updatedName, updatedQuantity, updatedSupplier, updatedTags, updatedMaxQuantity, metric);
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EditCommand)) {
            return false;
        }

        // state check
        RemoveCommand r = (RemoveCommand) other;
        return index.equals(r.index)
                && quantity.equals(r.quantity);
    }

}
