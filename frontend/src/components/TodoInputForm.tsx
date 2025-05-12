import {useState} from "react";
import "./TodoInputForm.scss";
import {AxiosError} from "axios";
import {isValidationErrorDTO} from "../types/ValidationErrorDTO.ts";
import {DiffViewer} from "./DiffViewer.tsx";
import {usePageLayoutContext} from "../context/PageLayoutContext.ts";

type Props = {
    disabled?: boolean;
    onSubmit: (_e: React.FormEvent<HTMLFormElement>, todoString: string, force?: boolean) => Promise<void>;
    clearError?: () => void;
}

const TodoInputForm = ({onSubmit, disabled, clearError}: Props) => {
    const [todoString, setTodoString] = useState<string>('');

    const {showModal} = usePageLayoutContext();

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setTodoString(e.currentTarget.value);
    }

    const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();

        let text = todoString;

        const handleCorrectionUpdate = (newTodoString: string)=> {
            text = newTodoString;
            setTodoString(text);
        }

        try {
            await onSubmit(e, todoString);
            setTodoString('');
        } catch(error) {
            if (error instanceof AxiosError && error.status === 422
                && isValidationErrorDTO(error.response?.data) && error.response.data.spellingValidation.errors.length > 0) {
                const errors = error.response.data.spellingValidation.errors;
                const original = errors[0].fullText;
                const corrected = errors[errors.length - 1].fullCorrectedText;
                const descriptions: Record<string, string> = {};
                errors.forEach(error => {
                    descriptions[error.originalTextPassage] = error.description;
                });

                if (clearError) {
                    clearError()
                }
                try {
                    const result = await showModal(
                        <DiffViewer
                            original={original}
                            corrected={corrected}
                            onAccept={handleCorrectionUpdate}
                            onReject={handleCorrectionUpdate}
                            descriptions={descriptions}
                        />,
                        "Rechtschreibfehler erkannt",
                        "Todo speichern",
                        undefined,
                        () => text,
                        () => text
                        );
                    await onSubmit(e, result + "", true);
                    setTodoString('');
                } catch (result) {
                    setTodoString(result + "");
                }
            }
        }
    }

    return (
        <form onSubmit={handleSubmit}>
            <div className="input-widget input-inline-button">
                <label htmlFor="todo">Todo:</label>
                <input name="todo" id="todo" value={todoString} type="text" onChange={handleChange} disabled={disabled} />
                <button type="submit">Hinzufügen</button>
            </div>
        </form>
    )
}

export default TodoInputForm;