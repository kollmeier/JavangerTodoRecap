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
}

export function usePageLayout() {
    const [state, setState] = useState<StateProps>({
        mainNav: [],
        subNav: [],
        actions: undefined,
        sidebar: undefined,
        subHeader: undefined,
        footer: undefined,
        header: undefined
    });

    return {
        header: state.header,
        subHeader: state.subHeader,
        footer: state.footer,
        mainNav: state.mainNav,
        subNav: state.subNav,
        actions: state.actions,
        sidebar: state.sidebar,
        setHeader: (header: ReactNode) => setState(prev => ({ ...prev, header })),
        setSubHeader: (subHeader?: ReactNode) => setState(prev => ({ ...prev, subHeader })),
        setFooter: (footer: ReactNode) => setState(prev => ({ ...prev, footer })),
        setMainNav: (mainNav: NavItem[]) => setState(prev => ({ ...prev, mainNav })),
        setSubNav: (subNav: NavItem[]) => setState(prev => ({ ...prev, subNav })),
        setActions: (actions: ReactNode) => setState(prev => ({ ...prev, actions })),
        setSidebar: (sidebar: ReactNode) => setState(prev => ({ ...prev, sidebar }))
    };
}