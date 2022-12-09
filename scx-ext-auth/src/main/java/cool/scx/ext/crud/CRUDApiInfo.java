package cool.scx.ext.crud;

import cool.scx.core.base.BaseModel;
import cool.scx.ext.crud.annotation.UseCRUDApi;

/**
 * <p>CRUDApiInfo class.</p>
 *
 * @author scx567888
 * @version 1.10.8
 */
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
    public final boolean list;

    /**
     * a
     */
    public final boolean info;

    /**
     * a
     */
    public final boolean add;

    /**
     * a
     */
    public final boolean update;

    /**
     * a
     */
    public final boolean delete;

    /**
     * a
     */
    public final boolean batchDelete;

    /**
     * a
     */
    public final boolean checkUnique;

    /**
     * <p>Constructor for CRUDApiInfo.</p>
     *
     * @param useCRUDApi     a {@link cool.scx.ext.crud.annotation.UseCRUDApi} object
     * @param baseModelClass a {@link java.lang.Class} object
     */
    public CRUDApiInfo(UseCRUDApi useCRUDApi, Class<BaseModel> baseModelClass) {
        this.baseModelClass = baseModelClass;
        this.baseModelName = baseModelClass.getSimpleName().toLowerCase();
        this.list = useCRUDApi.list();
        this.info = useCRUDApi.info();
        this.add = useCRUDApi.add();
        this.update = useCRUDApi.update();
        this.delete = useCRUDApi.delete();
        this.batchDelete = useCRUDApi.batchDelete();
        this.checkUnique = useCRUDApi.checkUnique();
    }

    /**
     * <p>hasThisApi.</p>
     *
     * @param crudApiType a {@link cool.scx.ext.crud.CRUDApiType} object
     * @return a boolean
     */
    public boolean hasThisApi(CRUDApiType crudApiType) {
        return switch (crudApiType) {
            case LIST -> list;
            case INFO -> info;
            case ADD -> add;
            case UPDATE -> update;
            case DELETE -> delete;
            case BATCH_DELETE -> batchDelete;
            case CHECK_UNIQUE -> checkUnique;
        };
    }

}
