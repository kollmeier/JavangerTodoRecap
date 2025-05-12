import React, { PropsWithChildren, ReactNode, useEffect } from "react";
import "./Modal.scss";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faClose} from "@fortawesome/free-solid-svg-icons";

type ModalProps = PropsWithChildren<{
    isOpen: boolean;
    title?: ReactNode;
    acceptButtonLabel?: string;
    moreButtons?: ReactNode;
    onClose?: () => void;
    onAccept?: () => void;
    footer?: ReactNode;
}>;

export const Modal: React.FC<ModalProps> = ({
                                                isOpen,
                                                title,
                                                onClose,
                                                onAccept,
                                                children,
                                                footer,
                                                acceptButtonLabel,
                                                moreButtons
                                            }) => {
    useEffect(() => {
        function onKeyDown(e: KeyboardEvent) {
            if (e.key === "Escape" && onClose) onClose()
            return;
        }
        if (isOpen) {
            document.addEventListener("keydown", onKeyDown);
        }
        return () => document.removeEventListener("keydown", onKeyDown);
    }, [isOpen, onClose]);

    if (!isOpen) return null;

    return (
        <div className="modal-backdrop" onClick={onClose}>
            <div className="modal-content" onClick={e => e.stopPropagation()}>
                {title && <div className="modal-header"><h2>{title}</h2></div>}
                <div className="modal-body">{children}</div>
                {footer && <div className="modal-footer">{footer}</div>}
                <button className="modal-close" onClick={onClose}>
                   <FontAwesomeIcon icon={faClose} />
                </button>
                <button className="modal-accept" onClick={onAccept}>
                    {acceptButtonLabel || "Annehmen"}
                </button>
                {moreButtons && moreButtons}
            </div>
        </div>
    );
};