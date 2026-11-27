package fr.alltech.eca.service.common;

/**
 * Centralise les définitions des séquences utilisées pour générer
 * les identifiants des différentes entités de l'application ECA.
 */
public final class SequenceDefinitions {

    private SequenceDefinitions() {
    }

    // Agences
    public static final int AGENCY_INITIAL_VALUE = 5;
    public static final String AGENCY_SEQUENCE = "agency";
    public static final String AGENCY_PREFIX = "AG";

    // Clients
    public static final int CLIENT_INITIAL_VALUE = 9;
    public static final String CLIENT_SEQUENCE = "client";
    public static final String CLIENT_PREFIX = "CLI";

    // Collaborateurs
    public static final int COLLABORATOR_INITIAL_VALUE = 16;
    public static final String COLLABORATOR_SEQUENCE = "collaborator";
    public static final String COLLABORATOR_PREFIX = "COL";

    // Affectations collaborateurs / missions
    public static final int COLLABORATOR_MISSION_INITIAL_VALUE = 14;
    public static final String COLLABORATOR_MISSION_SEQUENCE = "collaboratorMission";
    public static final String COLLABORATOR_MISSION_PREFIX = "CM";

    // Compétences
    public static final int COMPETENCE_INITIAL_VALUE = 377;
    public static final String COMPETENCE_SEQUENCE = "competence";
    public static final String COMPETENCE_PREFIX = "COMP";

    // Associations compétences / domaines
    public static final int COMPETENCE_DOMAIN_INITIAL_VALUE = 377;
    public static final String COMPETENCE_DOMAIN_SEQUENCE = "competenceDomain";
    public static final String COMPETENCE_DOMAIN_PREFIX = "CD";

    // Domaines
    public static final int DOMAIN_INITIAL_VALUE = 15;
    public static final String DOMAIN_SEQUENCE = "domain";
    public static final String DOMAIN_PREFIX = "DOM";

    // Niveaux d'expérience
    public static final int EXPERIENCE_LEVEL_INITIAL_VALUE = 4;
    public static final String EXPERIENCE_LEVEL_SEQUENCE = "experienceLevel";
    public static final String EXPERIENCE_LEVEL_PREFIX = "EXP";

    // Missions
    public static final int MISSION_INITIAL_VALUE = 22;
    public static final String MISSION_SEQUENCE = "mission";
    public static final String MISSION_PREFIX = "MIS";

    // Notifications
    public static final int NOTIFICATION_INITIAL_VALUE = 1;
    public static final String NOTIFICATION_SEQUENCE = "notification";
    public static final String NOTIFICATION_PREFIX = "NOTIF";

    // Catégories de métiers
    public static final int PROFESSION_CATEGORY_INITIAL_VALUE = 15;
    public static final String PROFESSION_CATEGORY_SEQUENCE = "professionCategory";
    public static final String PROFESSION_CATEGORY_PREFIX = "CAT";

    // Métiers
    public static final int PROFESSION_INITIAL_VALUE = 62;
    public static final String PROFESSION_SEQUENCE = "profession";
    public static final String PROFESSION_PREFIX = "PROF";

    // Associations métiers / compétences
    public static final int PROFESSION_COMPETENCE_INITIAL_VALUE = 878;
    public static final String PROFESSION_COMPETENCE_SEQUENCE = "professionCompetence";
    public static final String PROFESSION_COMPETENCE_PREFIX = "PC";

    // Rôles
    public static final int ROLE_INITIAL_VALUE = 5;
    public static final String ROLE_SEQUENCE = "role";
    public static final String ROLE_PREFIX = "ROLE";

    // Catégories d'outils
    public static final int TOOL_CATEGORY_INITIAL_VALUE = 4;
    public static final String TOOL_CATEGORY_SEQUENCE = "toolCategory";
    public static final String TOOL_CATEGORY_PREFIX = "TC";

    // Outils
    public static final int TOOL_INITIAL_VALUE = 4;
    public static final String TOOL_SEQUENCE = "tool";
    public static final String TOOL_PREFIX = "TOOL";
}