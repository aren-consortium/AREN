package fr.lirmm.aren.exception;

/**
 * Thrown if an error occurs on entity insertion in DB
 *
 * @author Florent Descroix {@literal <florentdescroix@posteo.net>}
 */
public class InsertEntityException extends AbstractException {

    /**
     *
     */
    private static final long serialVersionUID = 8631881926079551080L;

    /**
     *
     * @param propertyName
     * @return
     */
    public static InsertEntityException MANDATORY_PROPERTY(String propertyName) {
        InsertEntityException iee = new InsertEntityException("mandatory_property");
        iee.details.put("propertyName", propertyName);
        return iee;
    }

    /**
     *
     * @param keyName
     * @param keyValue
     * @return
     */
    public static InsertEntityException DUPLICATE_KEY(String keyName, String keyValue) {
        InsertEntityException iee = new InsertEntityException("duplicate_key");
        iee.details.put("keyName", keyName);
        iee.details.put("keyValue", keyValue);
        return iee;
    }

    /**
     *
     * @return
     */
    public static InsertEntityException INVALID_PARENT() {
        return new InsertEntityException("invalid_parent");
    }

    /**
     *
     * @param details
     * @return
     */
    public static InsertEntityException OTHER(String details) {
        return new InsertEntityException(details);
    }

    /**
     *
     * @param string
     */
    private InsertEntityException(String string) {
        super(string);
    }
}
