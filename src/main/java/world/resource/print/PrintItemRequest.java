package world.resource.print;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


/**
 * Request to print an item at a given quantity.
 */
@Getter
@Setter
@AllArgsConstructor
public class PrintItemRequest {
    private PrintDesignCode designCode;
    private PrintableResourceCode resourceCode;
    private int quantity;
    private Size size;
}
