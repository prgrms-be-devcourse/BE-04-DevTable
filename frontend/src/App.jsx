import './App.css';
import { createBrowserRouter, RouterProvider } from 'react-router-dom';
import NotFound from './pages/NotFound';
import UserPage from './pages/UserPage';
import OnwerPage from './pages/OnwerPage';
import Root from './pages/Root';
import Main from './Main';

const router = createBrowserRouter([
  {
    path: '/',
    element: <Root />,
    errorElement: <NotFound />,
    children: [
      { index: true, element: <Main /> },
      { path: 'Userpage', element: <UserPage /> },
      { path: 'OwnerPage', element: <OnwerPage /> },
    ],
  },
]);

function App() {
  return <RouterProvider router={router} />;
}

export default App;
