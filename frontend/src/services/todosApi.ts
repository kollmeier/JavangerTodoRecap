import {isTodoDTO, TodoDTO} from "../types/TodoDTO.ts";
import axios from "axios";
import {TodoInputDTO} from "../types/TodoInputDTO.ts";

export const todosApi = {
    baseUrl: '/api/todo',

    cancelableGetAllRef: null as AbortController | null,
    cancelableAddTodoRef: null as AbortController | null,
    cancelableUndoRef: null as AbortController | null,
    cancelableRedoRef: null as AbortController | null,
    cancelableUpdateTodoRef: {} as Record<string, AbortController | null>,
    cancelableDeleteTodoRef: {} as Record<string, AbortController | null>,

    async getAll(): Promise<TodoDTO[]> {
        this.cancelableGetAllRef?.abort();
        this.cancelableGetAllRef = new AbortController();

        try {
            const response = await axios.get(this.baseUrl, {
                signal: this.cancelableGetAllRef.signal
            });
            if (Array.isArray(response.data) && response.data.every(isTodoDTO)) {
                return response.data;
            }
        } catch (error) {
            if (axios.isCancel(error)) {
                return [];
            }
        }
        throw "Ungültige Antwort beim Laden der Todos";
    },

    async add(todo: TodoInputDTO) {
        this.cancelableAddTodoRef?.abort();
        this.cancelableAddTodoRef = new AbortController();

        try {
            const response = await axios.post(this.baseUrl, todo, {
                signal: this.cancelableAddTodoRef.signal});
            if (isTodoDTO(response.data)) {
                return response.data;
            }
        } catch (error) {
            if (axios.isCancel(error)) {
                return null;
            }
            throw error;
        }
        throw "Ungültige Antwort beim Speichern des Todos";
    },

    async update(todo: TodoInputDTO, id: string) {
        this.cancelableUpdateTodoRef[id]?.abort();
        this.cancelableUpdateTodoRef[id] = new AbortController();

        try {
            const response = await axios.put(`${this.baseUrl}/${id}`, todo, {
                signal: this.cancelableUpdateTodoRef[id].signal});
            if (isTodoDTO(response.data)) {
                return response.data;
            }
        } catch (error) {
            if (axios.isCancel(error)) {
                return null;
            }
            throw error;
        }
        throw "Ungültige Antwort beim Speichern des Todos";
    },

    async delete(id: string) {
        this.cancelableDeleteTodoRef[id]?.abort();
        this.cancelableDeleteTodoRef[id] = new AbortController();

        try {
            const response = await axios.delete(`${this.baseUrl}/${id}`, {
                signal: this.cancelableDeleteTodoRef[id].signal});

            if (response.status === 204) {
                return true;
            }
        } catch (error) {
            if (axios.isCancel(error)) {
                return false;
            }
            throw error;
        }
        throw "Ungültige Antwort beim Löschen des Todos";
    },

    async undo() {
        this.cancelableUndoRef?.abort();
        this.cancelableUndoRef = new AbortController();
        try {
            const response = await axios.post(`${this.baseUrl}/undo`, null, {
                signal: this.cancelableUndoRef.signal});
            if (Array.isArray(response.data) && response.data.every(isTodoDTO)) {
                return response.data;
            }
        } catch (error) {
            if (axios.isCancel(error)) {
                return null;
            }
            throw error;
        }
        throw "Ungültige Antwort bei Undo, versuchen Sie die Seite neu zu laden.";
    },

    async redo() {
        this.cancelableRedoRef?.abort();
        this.cancelableRedoRef = new AbortController();
        try {
            const response = await axios.post(`${this.baseUrl}/redo`, null, {
                signal: this.cancelableRedoRef.signal});
            if (Array.isArray(response.data) && response.data.every(isTodoDTO)) {
                return response.data;
            }
        } catch (error) {
            if (axios.isCancel(error)) {
                return null;
            }
            throw error;
        }
        throw "Ungültige Antwort bei Redo, versuchen Sie die Seite neu zu laden.";
    }

}