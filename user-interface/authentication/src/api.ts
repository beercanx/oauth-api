import LoginForm from "./LoginForm";

export interface CsrfToken {
    csrfToken: string
}

export async function fetchCsrfToken(endpoint: string): Promise<CsrfToken> {
    return window
        .fetch(endpoint, {
            method: "GET",
            headers: {'accept': 'application/json'},
            credentials: "include"
        })
        .then(response => response.json());
}

export interface AuthenticationResult {
    type: "success" | "failure";
    username?: string;
}

export async function fetchAuthentication(state: LoginForm.State, endpoint: string): Promise<AuthenticationResult> {
    return window
        .fetch(endpoint, {
            method: "POST",
            headers: {'content-type': 'application/json'},
            credentials: "include",
            body: JSON.stringify({
                username: state.username,
                password: state.password.split(""),
                csrfToken: state.csrfToken
            })
        })
        .then(response => response.json())
}