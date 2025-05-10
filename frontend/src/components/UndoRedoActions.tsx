import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faRedo, faUndo} from "@fortawesome/free-solid-svg-icons";
import "./UndoRedoActions.scss";

type Props = {
    undo: () => Promise<void>;
    redo: () => Promise<void>;
}

export const UndoRedoActions = ({undo, redo}: Props) => {
    return (
        <>
            <button onClick={undo}><FontAwesomeIcon icon={faUndo}/></button>
            <button onClick={redo}><FontAwesomeIcon icon={faRedo}/></button>
        </>
    )}