import React, {ChangeEvent, FormEvent, MouseEvent} from "react";

declare namespace LoginForm {

    interface Props {
        authenticationEndpoint: string
    }

    interface State {
        username: string
        password: string
        csrfToken: string
        redirectUri: string | null
    }
}

export class LoginForm extends React.Component<LoginForm.Props, LoginForm.State> {

    static defaultProps = {
        authenticationEndpoint: "/authentication",
    }

    constructor(props: LoginForm.Props) {
        super(props);

        this.state = {
            username: "",
            password: "",
            csrfToken: "",
            redirectUri: new URLSearchParams(document.location.search).get("redirectUri")
        };

        this.handleChangeUsername = this.handleChangeUsername.bind(this);
        this.handleChangePassword = this.handleChangePassword.bind(this);
        this.handleAbort = this.handleAbort.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
    }

    async componentDidMount() {
        await this.getCsrfToken();
    }

    async getCsrfToken() {
        console.info("Getting a CSRF token");
        // Alternative would be to tie this to the hosting service, and it has to provide a CSRF token in the page.
        await window
            .fetch(this.props.authenticationEndpoint, {
                method: "GET",
                headers: {'accept': 'application/json'},
                credentials: "include"
            })
            .then(response => response.json())
            .then(result => {
                console.info("CSRF token deserialized:", result)
                this.setState({csrfToken: result.csrfToken})
            })
            .catch(exception => console.error("CSRF token error:", exception));
    }

    handleChangeUsername(event: ChangeEvent<HTMLInputElement>) {
        this.setState({username: event.target.value});
    }

    handleChangePassword(event: ChangeEvent<HTMLInputElement>) {
        this.setState({password: event.target.value});
    }

    handleAbort(event: MouseEvent<HTMLButtonElement>) {
        event.preventDefault();
        console.log("Authentication Aborted!");
        this.setState({username: "", password: ""});
        // TODO - Redirect back with abort=true
        // TODO - What do we do about any existing "Authenticated-Customer" markers if we allow logins when we know who they are.
    }

    async handleSubmit(event: FormEvent<HTMLFormElement>) {
        event.preventDefault();
        console.log("Authentication Submitted!");

        await window
            .fetch(this.props.authenticationEndpoint, {
                method: "POST",
                headers: {'content-type': 'application/json'},
                credentials: "include",
                body: JSON.stringify({
                    username: this.state.username,
                    password: this.state.password.split(""),
                    csrfToken: this.state.csrfToken
                })
            })
            .then(response => response.json())
            .then(result => {
                console.info("Authentication deserialized:", result)
                if (this.state.redirectUri != null) {
                    window.location.href = this.state.redirectUri
                } else {
                    console.error("Missing redirectUri")
                }
            })
            .catch(exception => console.error("Authentication error:", exception));

        // TODO - Handle invalid CSRF token by getting a new one
        // TODO - Handle error responses in the UI
    }

    // TODO - Detect another tab / window completing the authentication and redirect back to /authorise if this happens.
    //        Aka the "Google" method when your asked to sign into multiple products when your authentication session expired.

    render() {
        return <>
            <h1 className="text-center">Authentication</h1>

            <form id="login-form" onSubmit={this.handleSubmit}>

                <input type="hidden" name="csrf_token" value={this.state.csrfToken}/>

                <div className="mb-3">
                    <label className="form-label" htmlFor="username">Username</label>
                    <input className="form-control" type="text" name="username" placeholder="Enter username"
                           autoComplete="on" value={this.state.username} onChange={this.handleChangeUsername}/>
                </div>

                <div className="mb-3">
                    <label className="form-label" htmlFor="password">Password</label>
                    <input className="form-control" type="password" name="password" placeholder="Password"
                           autoComplete="on" value={this.state.password} onChange={this.handleChangePassword}/>
                </div>

                <div className="row">

                    <div className="col-sm">
                        <button className="btn btn-primary w-100" name="login" value="login" type="submit">Login</button>
                    </div>

                    <div className="col-sm">
                        <button className="btn btn-primary w-100" name="abort" value="abort" type="button" onClick={this.handleAbort}>Abort</button>
                    </div>
                </div>
            </form>
        </>;
    }
}

export default LoginForm;