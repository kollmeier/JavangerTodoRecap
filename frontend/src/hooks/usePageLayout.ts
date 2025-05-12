import {ReactNode, useState} from "react";

export type NavItem = {
    label: string;
    href?: string;
    onClick?: () => void;
    children?: NavItem[];
};

export type PageLayoutApi = ReturnType<typeof usePageLayout>;

type StateProps = {
    header?: ReactNode;
    subHeader?: ReactNode;
    footer?: ReactNode;
    mainNav: NavItem[];
    subNav?: NavItem[];
    actions?: ReactNode;
    sidebar?: ReactNode;
    modal?: ReactNode;
    modalTitle?: ReactNode;
    modalAcceptButtonLabel?: string;
    modalMoreButtons?: ReactNode;
    handleModalAccept?: () => void,
    handleModalCancel?: () => void
}

export function usePageLayout() {
    const [state, setState] = useState<StateProps>({
        mainNav: [],
        subNav: [],
        actions: undefined,
        sidebar: undefined,
        subHeader: undefined,
        footer: undefined,
        header: undefined,
        modal: undefined,
        modalTitle: undefined,
        modalAcceptButtonLabel: undefined,
        modalMoreButtons: undefined,
        handleModalAccept: undefined,
        handleModalCancel: undefined
    });

    const showModal = <T, R>(content: ReactNode, modalTitle?: ReactNode, acceptButtonLabel?: string, moreButtons?: ReactNode, onResolve?: () => T, onReject?: () => R): Promise<T | R | boolean> => {
        return new Promise((resolve, reject) => {
            setHandleModalAccept(() => {
                setModal(undefined);
                resolve(onResolve ? onResolve() : true);
            });
            setHandleModalCancel(() => {
                setModal(undefined);
                reject(onReject ? onReject() : false);
            });
            setModalTitle(modalTitle);
            setModalAcceptButtonLabel(acceptButtonLabel);
            setModalMoreButtons(moreButtons);
            setModal(content);
        });
    }

    const setModal = (modal: ReactNode) =>
        setState(prev => ({ ...prev, modal }))

    const setHandleModalAccept = (handler: () => void) =>
        setState(prev => ({ ...prev, handleModalAccept: handler }))

    const setHandleModalCancel = (handler: () => void) =>
        setState(prev => ({ ...prev, handleModalCancel: handler }))

    const setModalTitle = (modalTitle?: ReactNode) =>
        setState(prev => ({ ...prev, modalTitle }))

    const setModalAcceptButtonLabel = (modalAcceptButtonLabel?: string) =>
        setState(prev => ({ ...prev, modalAcceptButtonLabel }))

    const setModalMoreButtons = (modalMoreButtons?: ReactNode) =>
        setState(prev => ({ ...prev, modalMoreButtons }))

    return {
        header: state.header,
        subHeader: state.subHeader,
        footer: state.footer,
        mainNav: state.mainNav,
        subNav: state.subNav,
        actions: state.actions,
        sidebar: state.sidebar,
        modal: state.modal,
        modalTitle: state.modalTitle,
        modalAcceptButtonLabel: state.modalAcceptButtonLabel,
        modalMoreButtons: state.modalMoreButtons,
        setHeader: (header: ReactNode) => setState(prev => ({ ...prev, header })),
        setSubHeader: (subHeader?: ReactNode) => setState(prev => ({ ...prev, subHeader })),
        setFooter: (footer: ReactNode) => setState(prev => ({ ...prev, footer })),
        setMainNav: (mainNav: NavItem[]) => setState(prev => ({ ...prev, mainNav })),
        setSubNav: (subNav: NavItem[]) => setState(prev => ({ ...prev, subNav })),
        setActions: (actions: ReactNode) => setState(prev => ({ ...prev, actions })),
        setSidebar: (sidebar: ReactNode) => setState(prev => ({ ...prev, sidebar })),
        setModalMoreButtons: (modalMoreButtons: ReactNode) => setState(prev => ({ ...prev, modalMoreButtons })),
        showModal,
        acceptModal: state.handleModalAccept,
        cancelModal: state.handleModalCancel
    };
}