package dungeonrunner.model;

/**
 * @author Michael Lieshoff
 */
public class Structure extends PlayerContainer {

    private StructureInfo structureInfo;

    public Structure(int id, StructureInfo structureInfo) {
        super(id);
        this.structureInfo = structureInfo;
    }

    public StructureInfo getStructureInfo() {
        return structureInfo;
    }

}
