import {StatusType} from "../types/StatusType.ts";

const todoStatusDisplayConverter = {
    shortDisplayString: (status: StatusType) => {
        switch (status.toLowerCase()) {
            case 'open':
                return "offen";
            case 'in_progress':
                return "laufend";
            case 'done':
                return "erledigt"
        }
    },
    navDisplayLink: (status?: StatusType) => {
        switch (status?.toLowerCase()) {
            case 'open':
                return "Offen";
            case 'in_progress':
                return "Laufend";
            case 'done':
                return "Erledigt";
            default:
                return "To-Dos";
        }
    },
    longDisplayString: (status: StatusType) => {
        switch (status.toLowerCase()) {
            case 'open':
                return "Offene To-Dos";
            case 'in_progress':
                return "Laufende To-Dos";
            case 'done':
                return "Erledigte To-Dos";
            default:
                return "Alle To-Dos";
        }
    }
}

export default todoStatusDisplayConverter