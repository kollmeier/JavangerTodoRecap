import { StatusType } from "./StatusType";

export type TodoDTO = {
  id: string;
  status: StatusType;
  description: string;
  createdAt: string;
};

export function isTodoDTO(obj: unknown): obj is TodoDTO {
    return typeof obj === 'object' && obj !== null
        && 'id' in obj
        && 'status' in obj
        && 'description' in obj
        && 'createdAt' in obj
        && typeof obj.description === 'string'
        && typeof obj.id === 'string'
        && typeof obj.status === 'string'
        && ['OPEN', 'IN_PROGRESS', 'DONE'].includes(obj.status)
        && typeof obj.createdAt === 'string';

}