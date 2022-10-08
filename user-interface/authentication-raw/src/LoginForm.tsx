import React, {ChangeEvent, FormEvent, MouseEvent} from "react";

export interface Props {
    authenticationEndpoint: string
    csrfToken: string
}

export interface State {
    username: string
    password: string
}

export class LoginForm extends React.Component<Props, State> {

    constructor(props: Props) {
        super(props);

        this.state = {username: "", password: ""};

        this.handleChangeUsername = this.handleChangeUsername.bind(this);
        this.handleChangePassword = this.handleChangePassword.bind(this);
        this.handleAbort = this.handleAbort.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
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
    }

    // TODO - Replace with HTML form submit to enable server side redirect and reduce the UI code?
    async handleSubmit(event: FormEvent<HTMLFormElement>) {
        event.preventDefault();
        console.log("Authentication Submitted!");

        await window
            .fetch(this.props.authenticationEndpoint, {
                method: "POST",
                headers: {
                    'content-type': 'application/json',
                },
                credentials: "include",
                body: JSON.stringify({
                    username: this.state.username,
                    password: this.state.password.split("")
                })
            })
            .then(response => response.text())
            .then(body => JSON.parse(body))
            .then(result => console.log("Authentication Deserialized:", result))
            .catch(exception => console.error("Authentication Error:", exception));
    }

    render() {
        return (
            <>
                <h1 className="text-center">Authentication</h1>

                <form id="login-form" onSubmit={this.handleSubmit}>

                    <input type="hidden" name="csrf_token" value={this.props.csrfToken}/>

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
                            <button className="btn btn-primary w-100" name="login" value="login" type="submit">Login
                            </button>
                        </div>

                        <div className="col-sm">
                            <button className="btn btn-primary w-100" name="abort" value="abort" type="button"
                                    onClick={this.handleAbort}>Abort
                            </button>
                        </div>
                    </div>
                </form>
            </>
        );
    }
}

export default LoginForm;