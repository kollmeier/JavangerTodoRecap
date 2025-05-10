import ErrorDisplay from "./ErrorDisplay.tsx";
import {UndoRedoActions} from "./UndoRedoActions.tsx";
import TodoInputForm from "./TodoInputForm.tsx";

type Props = {
    undo: () => Promise<void>;
    redo: () => Promise<void>;
    error: string | null;
    onSubmit: (e: React.FormEvent<HTMLFormElement>, todoString: string) => Promise<void>;
    disabled?: boolean;
}

const Actions = ({undo, redo, error, onSubmit, disabled}: Props) => (
    <>
        <ErrorDisplay error={error} />
        <UndoRedoActions undo={undo} redo={redo} />
        <TodoInputForm onSubmit={onSubmit} disabled={disabled} />
    </>
);

export default Actions;