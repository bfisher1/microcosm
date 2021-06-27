package world.block;

import lombok.Getter;
import lombok.Setter;
import playground.World;

@Getter
@Setter
public class TrayBlock extends Block {

    private boolean forAssembly;

    public TrayBlock(int x, int y, World world) {
        super(x, y, world);
        setAnimation("tray.png");
        setFullyCoveringView(false);
    }

    public void setForAssembly(boolean forAssembly) {
        this.forAssembly = forAssembly;

        if (forAssembly) {
            setAnimation("assembly-tray.png");
        } else {
            setAnimation("tray.png");
        }
    }
}
