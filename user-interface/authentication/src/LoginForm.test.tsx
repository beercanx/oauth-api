import React, {act} from 'react';
import {render, screen} from '@testing-library/react';
import LoginForm from './LoginForm';
import fetchMock from "jest-fetch-mock";

test('renders login form', async () => {

    fetchMock.mockResponse(JSON.stringify({csrfToken: 'aardvark'}));

    await act(async () => {
        render(<LoginForm />);
    });

    const loginButton = screen.getByText(/Login/i);
    expect(loginButton).toBeInTheDocument();
});
