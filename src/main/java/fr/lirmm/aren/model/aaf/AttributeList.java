package fr.lirmm.aren.model.aaf;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

import fr.lirmm.aren.model.User.Authority;

/**
 * Part of a Request.
 * 
 * @author Florent Descroix {@literal <florentdescroix@posteo.net>}
 */
@XmlRootElement
public class AttributeList extends ArrayList<Attribute> {

    /**
     *
     */
    private static final long serialVersionUID = -5945543842036181814L;

    /**
     *
     * @param name
     * @return
     */
    public Set<String> getValuesOf(String name) {

        int i = indexOf(new Attribute(name));
        if (i == -1) {
            return new HashSet<>();
        }
        return get(i).getValues();
    }

    /**
     *
     * @param name
     * @return
     */
    public String getValueOf(String name) {

        int i = indexOf(new Attribute(name));
        if (i == -1) {
            return null;
        }
        return get(i).getValues().iterator().next();
    }

    /**
     *
     * @return
     */
    public String getFirstName() {
        return getValueOf(FicAlimMENESR.PERSONNE.FIRST_NAME);
    }

    /**
     *
     * @return
     */
    public String getLastName() {
        return getValueOf(FicAlimMENESR.PERSONNE.LAST_NAME);
    }

    /**
     *
     * @return
     */
    public String getEmail() {
        return getValueOf(FicAlimMENESR.PERSONNE.EMAIL);
    }

    /**
     *
     * @return
     */
    public Set<String> getClasses() {
        if (!getValuesOf(FicAlimMENESR.PERSONNE.CLASSES).isEmpty()) {
            return getValuesOf(FicAlimMENESR.PERSONNE.CLASSES);
        } else {
            return getValuesOf(FicAlimMENESR.STRUCTURE.CLASSES);
        }
    }

    /**
     *
     * @return
     */
    public Set<String> getGroups() {
        if (!getValuesOf(FicAlimMENESR.PERSONNE.GROUPES).isEmpty()) {
            return getValuesOf(FicAlimMENESR.PERSONNE.GROUPES);
        } else {
            return getValuesOf(FicAlimMENESR.STRUCTURE.GROUPES);
        }
    }

    /**
     *
     * @return
     */
    public String getInstitutionId() {
        return getValueOf(FicAlimMENESR.PERSONNE.INSTITUTION);
    }

    /**
     *
     * @return
     */
    public String getName() {
        return getValueOf(FicAlimMENESR.STRUCTURE.NAME);
    }

    /**
     *
     * @return
     */
    public String getCategory() {
        if (getValueOf(FicAlimMENESR.CATEGORY.PERSONNE) != null) {
            return getValueOf(FicAlimMENESR.CATEGORY.PERSONNE);
        } else if (getValueOf(FicAlimMENESR.CATEGORY.STRUCTURE) != null) {
            return getValueOf(FicAlimMENESR.CATEGORY.STRUCTURE);
        }
        return null;
    }

    /**
     *
     * @return
     */
    public String getType() {
        return getValueOf(FicAlimMENESR.STRUCTURE.TYPE);
    }

    /**
     *
     * @return
     */
    public String getAcademy() {
        return getValueOf(FicAlimMENESR.STRUCTURE.ACADEMY);
    }

    /**
     *
     * @return
     */
    public Authority getAuthority() {
        if (getCategory().equals(FicAlimMENESR.PERSONNE.ELEVE)) {
            return Authority.USER;
        } else if (getCategory().equals(FicAlimMENESR.PERSONNE.ENSEIGNANT) || getCategory().equals(FicAlimMENESR.PERSONNE.EDUC_NAT)) {
            return Authority.MODO;
        }
        return null;
    }

    /**
     *
     * @return
     */
    public boolean isUser() {
        return getValueOf(FicAlimMENESR.CATEGORY.PERSONNE) != null;
    }

    /**
     *
     * @return
     */
    public boolean isInstitution() {
        return getValueOf(FicAlimMENESR.CATEGORY.STRUCTURE) != null;
    }
}
