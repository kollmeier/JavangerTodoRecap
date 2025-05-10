import {Outlet} from "react-router-dom";
import {Flip, ToastContainer} from "react-toastify";
import {PageLayoutContextProvider} from "./context/PageLayoutContextProvider.tsx";
import todoStatusDisplayConverter from "./services/todoStatusDisplayConverter.ts";

const Layout = () => {

    return (
        <>
            <PageLayoutContextProvider
                    header={<h1>Javangers Easy ToDos</h1>}
                    mainNav={
                    [
                        {label: todoStatusDisplayConverter.navDisplayLink(), href: "/"},
                        {label: todoStatusDisplayConverter.navDisplayLink("open"), href: "/open"},
                        {label: todoStatusDisplayConverter.navDisplayLink("in_progress"), href: "/in_progress"},
                        {label: todoStatusDisplayConverter.navDisplayLink("done"), href: "/done"}
                    ]}
                >
                    <Outlet />
            </PageLayoutContextProvider>

            <ToastContainer
                aria-label="ToastContainer"
                position="top-left"
                autoClose={1200}
                hideProgressBar={false}
                newestOnTop={false}
                closeOnClick={true}
                rtl={false}
                draggable
                pauseOnHover
                theme="light"
                transition={Flip}
            />

        </>
    )
};

export default Layout;