import {expect, rs, test} from '@rstest/core';
import {render, screen} from '@testing-library/react';
import LoginForm from './LoginForm';
import {CsrfToken} from "./api";

rs.mock('./api', () => ({
    fetchCsrfToken: (): Promise<CsrfToken> => Promise.resolve({csrfToken: 'aardvark'}),
}));

test('renders login form', async () => {
    render(<LoginForm/>);
    expect(await screen.findByText(/Login/i)).toBeDefined();
});
