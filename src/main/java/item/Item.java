package item;

import lombok.Getter;
import lombok.Setter;
import util.IntLoc;

@Getter
@Setter
public class Item {
    private Itemable item;
    private int quantity;
    private IntLoc layoutOffset;
}
