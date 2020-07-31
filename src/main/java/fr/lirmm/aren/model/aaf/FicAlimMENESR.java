package fr.lirmm.aren.model.aaf;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Object to serialize XML AAF file.
 *
 * @author Florent Descroix {@literal <florentdescroix@posteo.net>}
 */
@XmlRootElement(name = "ficAlimMENESR")
public class FicAlimMENESR {

    /**
     *
     */
    public static class CATEGORY {

        /**
         *
         */
        public static String PERSONNE = "categoriePersonne";

        /**
         *
         */
        public static String STRUCTURE = "categorieStructure";
    }

    /**
     *
     */
    public static class PERSONNE {

        /**
         *
         */
        public static String ELEVE = "Eleve";

        /**
         *
         */
        public static String ENSEIGNANT = "PersRelEleve";

        /**
         *
         */
        public static String EDUC_NAT = "PersEducNat";

        /**
         *
         */
        public static String LAST_NAME = "sn";

        /**
         *
         */
        public static String FIRST_NAME = "givenName";

        /**
         *
         */
        public static String EMAIL = "mail";

        /**
         *
         */
        public static String CLASSES = "ENTEleveClasses";

        /**
         *
         */
        public static String GROUPES = "ENTEleveGroupes";

        /**
         *
         */
        public static String INSTITUTION = "ENTPersonStructRattach";
    }

    /**
     *
     */
    public static class STRUCTURE {

        /**
         *
         */
        public static String NAME = "ENTStructureNomCourant";

        /**
         *
         */
        public static String TYPE = "ENTStructureTypeStruct";

        /**
         *
         */
        public static String ACADEMY = "ENTServAcAcademie";

        /**
         *
         */
        public static String CLASSES = "ENTStructureClasses";

        /**
         *
         */
        public static String GROUPES = "ENTStructureGroupes";
    }

    @XmlElements({
        @XmlElement(name = "addRequest", type = AddRequest.class),
        @XmlElement(name = "modifyRequest", type = ModifyRequest.class),
        @XmlElement(name = "deleteRequest", type = DeleteRequest.class),})
    private List<AbstractRequest> requests;

    /**
     *
     * @return
     */
    @XmlTransient
    public List<AbstractRequest> getRequest() {
        return requests;
    }

    /**
     *
     * @param requests
     */
    public void setRequest(List<AbstractRequest> requests) {
        this.requests = requests;
    }

    /**
     *
     * @return
     */
    public int length() {

        int amount = requests.size();
        for (AbstractRequest req : requests) {
            if (req.getOperationalAttributes().isInstitution()) {
                amount += req.getAttributes().getClasses().size();
                amount += req.getAttributes().getGroups().size();
            }
        }
        return amount;
    }

}
