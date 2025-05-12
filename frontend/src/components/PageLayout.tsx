import React, {ReactNode, useEffect, useState} from "react";
import "./PageLayout.scss";
import {NavItem} from "../hooks/usePageLayout.ts";
import {NavLink} from "react-router-dom";
import {Modal} from "./Modal.tsx";
import {usePageLayoutContext} from "../context/PageLayoutContext.ts";

export type PageLayoutProps = {
    header?: ReactNode;
    subHeader?: ReactNode;
    footer?: ReactNode;
    mainNav: NavItem[];
    subNav?: NavItem[];
    actions?: ReactNode;
    sidebar?: ReactNode;
    modal?: ReactNode;
    children: ReactNode;
};

export const PageLayout: React.FC<PageLayoutProps> = (props) => {
    const { header, subHeader, footer, mainNav, subNav, actions, sidebar, children, modal } = props;
    const { acceptModal, cancelModal, modalTitle, modalAcceptButtonLabel, modalMoreButtons } = usePageLayoutContext();

    const [showModal, setShowModal] = useState(false);

    useEffect(() => {
        setShowModal(!!modal);
    }, [modal]);

    return (
        <>
        <div className="page-layout">
            {header && <header className="pl-header">
                {header}
                {subHeader && <h2>{subHeader}</h2>}
            </header>}
            <nav className="pl-nav">
                <ul>
                    {mainNav.map((item) => (
                        <li key={item.label}>
                            {item.href ? (
                                <NavLink to={item.href}>{item.label}</NavLink>
                            ) : (
                                <button onClick={item.onClick}>{item.label}</button>
                            )}
                        </li>
                    ))}
                </ul>
                {subNav && (
                    <ul className="pl-subnav">
                        {subNav.map((item) => (
                            <li key={item.label}>
                                {item.href ? (
                                    <NavLink to={item.href}>{item.label}</NavLink>
                                ) : (
                                    <button onClick={item.onClick}>{item.label}</button>
                                )}
                            </li>
                        ))}
                    </ul>
                )}
            </nav>
            <div className="pl-content-area">
                {sidebar && <aside className="pl-sidebar">{sidebar}</aside>}
                <main className="pl-main">
                    <div className="pl-page-actions">{actions}</div>
                    <div className="pl-content">{children}</div>
                </main>
            </div>
            {footer && <footer className="pl-footer">{footer}</footer>}
        </div>
        {(<Modal
            isOpen={showModal}
            onClose={cancelModal}
            onAccept={acceptModal}
            title={modalTitle}
            acceptButtonLabel={modalAcceptButtonLabel}
            moreButtons={modalMoreButtons}
        >
            {modal}
        </Modal>)}
        </>
    )
};

export default PageLayout;