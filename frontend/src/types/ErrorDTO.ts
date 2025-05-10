export type ErrorDTO = {
    error: string;
    message: string;
    status: string;
};

export function isErrorDTO(obj: unknown): obj is ErrorDTO {
    if (typeof obj !== 'object' || obj === null) return false;
    const o = obj as Record<string, unknown>;
    return typeof o.error === 'string' &&
        typeof o.message === 'string' &&
        typeof o.status === 'string';
}