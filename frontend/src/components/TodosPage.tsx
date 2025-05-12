import {useTodos} from "../hooks/useTodos.ts";
import {useParams} from "react-router-dom";
import {StatusType} from "../types/StatusType.ts";
import TodosList from "./TodosList.tsx";
import {useEffect} from "react";
import {usePageLayoutContext} from "../context/PageLayoutContext.ts";
import {TodoDTO} from "../types/TodoDTO.ts";
import todoStatusDisplayConverter from "../services/todoStatusDisplayConverter.ts";
import "./TodosPage.scss";
import {toast} from "react-toastify";
import Actions from "./Actions.tsx";

const TodosPage = () => {
    const {todos, saveTodo, updateTodo, removeTodo, loading, error, undo, redo, clearError} = useTodos();

    const {filter} = useParams<{filter: StatusType}>();

    const {setActions, setSubHeader} = usePageLayoutContext();

    const handleSubmit = async (_e: React.FormEvent<HTMLFormElement>, todoString: string, force?: boolean) => {
        return toast.promise(
            saveTodo({status: 'OPEN', description: todoString}, force),
            {
                pending: 'Speichere To-Do...',
                success: 'To-Do erfolgreich gespeichert',
                error: 'Beim Speichern ist ein Fehler aufgetreten',
            });
    }

    const handleDelete = async (id: string) => {
        return toast.promise(removeTodo(id),
            {
                pending: 'Lösche To-Do...',
                success: 'To-Do erfolgreich gelöscht',
                error: 'Beim Löschen ist ein Fehler aufgetreten',
            });
    }

    const handleUpdate = async (todo: TodoDTO) => {
        let nextStatus: StatusType = 'open';
        switch (todo.status.toLowerCase()) {
            case 'open':
                nextStatus = 'in_progress';
                break;
            case 'in_progress':
                nextStatus = 'done';
                break;
        }
        if (updateTodo) {
            return toast.promise(updateTodo({...todo, status: nextStatus.toUpperCase()}, todo.id),
                {
                    pending: 'Aktualisiere To-Do...',
                    success: 'To-Do erfolgreich aktualisiert',
                    error: 'Beim Aktualisieren ist ein Fehler aufgetreten',
                });
        }
    }

    useEffect(() => {
        setSubHeader(filter ? todoStatusDisplayConverter.longDisplayString(filter) : 'Alle To-Dos');
        setActions(filter ? undefined : (<Actions undo={undo} redo={redo} disabled={loading} error={error} clearError={clearError} onSubmit={handleSubmit} />));
    }, [filter, error, loading]);

    return (
    <>
        <div className="todos-page">
            {filter ? (
                <TodosList todos={todos.filter(todo => todo.status.toLowerCase() === filter)} updateTodo={filter === 'open' || filter === 'in_progress' ? handleUpdate : undefined} deleteTodo={filter === 'open' || filter === 'in_progress' ? undefined : handleDelete}/>
            ) : (
            <>
                <div>
                    <h2>{todoStatusDisplayConverter.longDisplayString('open')}</h2>
                    <TodosList todos={todos.filter(todo => todo.status.toLowerCase() === 'open')} updateTodo={handleUpdate}/>
                </div>
                <div>
                    <h2>{todoStatusDisplayConverter.longDisplayString('in_progress')}</h2>
                    <TodosList todos={todos.filter(todo => todo.status.toLowerCase() === 'in_progress')} updateTodo={handleUpdate}/>
                </div>
                <div>
                    <h2>{todoStatusDisplayConverter.longDisplayString('done')}</h2>
                    <TodosList todos={todos.filter(todo => todo.status.toLowerCase() === 'done')} deleteTodo={handleDelete}/>
                </div>
            </>
            )}
        </div>
    </>
)};

export default TodosPage;