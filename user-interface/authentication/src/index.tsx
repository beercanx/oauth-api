import {StrictMode} from "react";
import {createRoot} from 'react-dom/client';
import {LoginForm} from "./LoginForm";

createRoot(document.getElementById('root') as HTMLElement).render(<StrictMode><LoginForm/></StrictMode>);