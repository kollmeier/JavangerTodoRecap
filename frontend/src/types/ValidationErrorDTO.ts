export type ValidationErrorDTO = {
    error: string;
    message: string;
    status: string;
    spellingValidation: SpellingValidationDTO;
};

export function isValidationErrorDTO(obj: unknown): obj is ValidationErrorDTO {
    if (typeof obj !== 'object' || obj === null) return false;
    const o = obj as Record<string, unknown>;
    return typeof o.error === 'string' &&
        typeof o.message === 'string' &&
        typeof o.status === 'string' &&
        o.spellingValidation !== undefined &&
        isSpellingValidationDTO(o.spellingValidation);
}

export type SpellingValidationDTO = {
    errorCount: number;
    errors: SpellingValidationItemDTO[];
};

export function isSpellingValidationDTO(obj: unknown): obj is SpellingValidationDTO {
    if (typeof obj !== 'object' || obj === null) return false;
    const o = obj as Record<string, unknown>;
    return typeof o.errorCount === 'number' &&
        Array.isArray(o.errors) &&
        o.errors.every(isSpellingValidationItemDTO);
}

export type SpellingValidationItemDTO = {
    textPassagePosition: number;
    originalTextPassage: string;
    correctedTextPassage: string;
    fullText: string;
    fullCorrectedText: string;
    description: string;
};

export function isSpellingValidationItemDTO(obj: unknown): obj is SpellingValidationItemDTO {
    if (typeof obj !== 'object' || obj === null) return false;
    const o = obj as Record<string, unknown>;
    return typeof o.textPassagePosition === 'number' &&
        typeof o.originalTextPassage === 'string' &&
        typeof o.correctedTextPassage === 'string' &&
        typeof o.fullText === 'string' &&
        typeof o.fullCorrectedText === 'string' &&
        typeof o.description === 'string';
}