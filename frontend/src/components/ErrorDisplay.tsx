import "./ErrorDisplay.scss";

const ErrorDisplay = ({error}: {error: string | null}) => (
    <>
        {error && <div className="error">
            <span>{error}</span>
        </div>}
    </>
)

export default ErrorDisplay;