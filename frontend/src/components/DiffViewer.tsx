import React, {useEffect, useRef, useState} from "react";
import { diffWords } from "diff";
import "./DiffViewer.scss";
import * as Popover from "@radix-ui/react-popover";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faCancel, faCheck} from "@fortawesome/free-solid-svg-icons";
import {usePageLayoutContext} from "../context/PageLayoutContext.ts";

interface Props {
    original: string;
    corrected: string;
    descriptions?: Record<string, string>;
    onAccept?: (newString: string) => void;
    onReject?: (newString: string) => void;
}

export const DiffViewer: React.FC<Props> = ({ original, corrected, descriptions, onAccept, onReject }) => {
    const diff = diffWords(original, corrected);
    const [acceptedMap, setAcceptedMap] = useState<boolean[]>(diff.map(part => !part.added && !part.removed));
    const [rejectedMap, setRejectedMap] = useState<boolean[]>(Array.from({ length: diff.length }, () => false));
    const [openPopoverIndex, setOpenPopoverIndex] = useState<number | null>(null);

    const lastTimeOut = useRef<NodeJS.Timeout | null>(null);

    const {setModalMoreButtons} = usePageLayoutContext();

    useEffect(() => {
        setModalMoreButtons(<>
            <button className="button--secondary" onClick={rejectAll}>alle ablehnen</button>
            <button className="button--secondary" onClick={acceptAll}>alles korrigieren</button>
        </>)
    }, []);

    const acceptAll = () => {
        setAcceptedMap(Array.from({ length: diff.length }, () => true));
        setRejectedMap(Array.from({ length: diff.length }, () => false));
        setOpenPopoverIndex(null);
        if (onAccept) {
            onAccept(buildText(Array.from({ length: diff.length }, () => true)));
        }
    }

    const rejectAll = () => {
        setAcceptedMap(Array.from({ length: diff.length }, () => false));
        setRejectedMap(Array.from({ length: diff.length }, () => true));
        setOpenPopoverIndex(null);
        if (onReject) {
            onReject(buildText(Array.from({ length: diff.length }, () => false)));
        }
    }

    const closePopoverPerTimeout = ()=> {
        if (lastTimeOut.current) {
            clearTimeout(lastTimeOut.current);
        }
        lastTimeOut.current = setTimeout(() => {
            setOpenPopoverIndex(null);
        }, 800);
    }

    const setOpenPopoverIndexAndClearTimeout = (index: number)=> {
        setOpenPopoverIndex(index);
        if (lastTimeOut.current) {
            clearTimeout(lastTimeOut.current);
            lastTimeOut.current = null;
        }
    }

    const buildText = (accepted: boolean[]) => {
        return diff.map((part, i) => {
            if (part.removed) {
                return accepted[i] ? '' : part.value;
            } else if (part.added) {
                return accepted[i] ? part.value : '';
            }
            return part.value;
        }).join('');
    };

    const handleAccept = (index: number) => {
        const newAcceptedMap = [...acceptedMap];
        const newRejectedMap = [...rejectedMap];
        newAcceptedMap[index] = true;
        newRejectedMap[index] = false;
        if (diff[index].added && diff[index - 1] && diff[index - 1].removed) {
            newAcceptedMap[index - 1] = true;
            newRejectedMap[index - 1] = false;
        }
        setAcceptedMap(newAcceptedMap);
        setRejectedMap(newRejectedMap);
        setOpenPopoverIndex(null);

        if (onAccept) {
            onAccept(buildText(newAcceptedMap));
        }
    };

    const handleReject = (index: number) => {
        const newRejectedMap = [...rejectedMap];
        const newAcceptedMap = [...acceptedMap];
        newRejectedMap[index] = true;
        newAcceptedMap[index] = false;
        if (diff[index].added && diff[index - 1] && diff[index - 1].removed) {
            newRejectedMap[index - 1] = true;
            newAcceptedMap[index - 1] = false;
        }
        setRejectedMap(newRejectedMap);
        setAcceptedMap(newAcceptedMap);
        setOpenPopoverIndex(null);

        if (onReject) {
            onReject(buildText(newAcceptedMap));
        }
    };

    return (
        <p>
            {diff.map((part, i) => {
                if (!part.added && !part.removed) {
                    return (<span key={i}>{part.value}</span>)
                } else if (part.added) {
                    return (
                        <Popover.Root key={i} open={openPopoverIndex === i}
                        >
                            <Popover.Trigger asChild>
                                <span className="popover-trigger" tabIndex={0}
                                    style={{cursor: "pointer"}}
                                      onMouseEnter={() => setOpenPopoverIndexAndClearTimeout(i)}
                                      onMouseLeave={closePopoverPerTimeout}
                                >
                                    {acceptedMap[i] ? (
                                        <span className="correction"
                                              style={{background: "#e0ffe0"}}>
                                            {part.value}
                                        </span>
                                    ) : (
                                        <span
                                              className="validation-error">
                                            {diff[i - 1] && diff[i - 1].removed && diff[i - 1].value}
                                        </span>
                                    )}
                                    </span>
                            </Popover.Trigger>
                            <Popover.Portal>
                                <Popover.Content className="popover-content"
                                                 side="top"
                                                 align="start"
                                                 style={{ zIndex: 3000 }} // Hoch genug für Modal!
                                                 onMouseLeave={closePopoverPerTimeout}
                                                 onMouseEnter={() => setOpenPopoverIndexAndClearTimeout(i)}
                                >
                                    <div>
                                        <div><strong>Korrekturvorschlag</strong></div>
                                        <div>{descriptions && descriptions[diff[i-1]?.value] !== undefined ? descriptions[diff[i-1]?.value] : part.value}</div>
                                        <div style={{marginTop: 8}}>
                                            <button className={"button-accept " + (acceptedMap[i] ? "active" : "")}
                                                    onClick={() => handleAccept(i)}><FontAwesomeIcon icon={faCheck} />
                                            </button>
                                            <button className={"button-reject " + (rejectedMap[i] ? "active" : "")}
                                                    onClick={() => handleReject(i)} style={{marginLeft: 4}}><FontAwesomeIcon icon={faCancel} />
                                            </button>
                                        </div>
                                    </div>
                                    <Popover.Arrow/>
                                </Popover.Content>
                            </Popover.Portal>
                        </Popover.Root>
                    )
                }
            })}
        </p>
    );
};