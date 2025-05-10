import {useState} from "react";
import "./TodoInputForm.scss";

type Props = {
    disabled?: boolean;
    onSubmit: (_e: React.FormEvent<HTMLFormElement>, todoString: string) => Promise<void>;
}

const TodoInputForm = ({onSubmit, disabled}: Props) => {
    const [todoString, setTodoString] = useState<string>('');

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setTodoString(e.currentTarget.value);
    }

    const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        await onSubmit(e, todoString);
        setTodoString('');
    }

    return (
        <form onSubmit={handleSubmit}>
            <div className="input-widget input-inline-button">
                <label htmlFor="todo">Todo:</label>
                <input name="todo" id="todo" type="text" value={todoString} onChange={handleChange} disabled={disabled} />
                <button type="submit">Hinzufügen</button>
            </div>
        </form>
    )
}

export default TodoInputForm;