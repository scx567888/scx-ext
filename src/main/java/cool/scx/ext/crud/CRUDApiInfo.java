package cool.scx.ext.crud;

import cool.scx.base.BaseModel;
import cool.scx.ext.crud.annotation.UseCRUDApi;

public final class CRUDApiInfo {

    /**
     * a
     */
    public final Class<BaseModel> baseModelClass;

    /**
     * a
     */
    public final String baseModelName;

    /**
     * a
     */
    public boolean list;

    /**
     * a
     */
    public boolean info;

    /**
     * a
     */
    public boolean save;

    /**
     * a
     */
    public boolean update;

    /**
     * a
     */
    public boolean delete;

    /**
     * a
     */
    public boolean batchDelete;

    /**
     * a
     */
    public boolean revokeDelete;

    /**
     * a
     */
    public boolean checkUnique;

    public CRUDApiInfo(UseCRUDApi useCRUDApi, Class<BaseModel> baseModelClass) {
        this.baseModelClass = baseModelClass;
        this.baseModelName = baseModelClass.getSimpleName().toLowerCase();
        this.list = useCRUDApi.list();
        this.info = useCRUDApi.info();
        this.save = useCRUDApi.save();
        this.update = useCRUDApi.update();
        this.delete = useCRUDApi.delete();
        this.batchDelete = useCRUDApi.batchDelete();
        this.revokeDelete = useCRUDApi.revokeDelete();
        this.checkUnique = useCRUDApi.checkUnique();
    }

    public boolean hasThisApi(CRUDApiType crudApiType) {
        return switch (crudApiType) {
            case LIST -> list;
            case INFO -> info;
            case SAVE -> save;
            case UPDATE -> update;
            case DELETE -> delete;
            case BATCH_DELETE -> batchDelete;
            case REVOKE_DELETE -> revokeDelete;
            case CHECK_UNIQUE -> checkUnique;
        };
    }

}