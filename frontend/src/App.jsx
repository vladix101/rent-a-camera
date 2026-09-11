import './App.css'
import Header from './pages/header/Header.jsx'
import Home from './pages/home/Home.jsx'
import Login from './pages/login/Login.jsx'
import Register from './pages/register/Register.jsx'
import MyRentals from './pages/myRentals/MyRentals.jsx'
import AddCameraPage from './pages/camera/AddCameraPage.jsx'
import ClientsPage from './pages/clients/ClientsPage.jsx'
import StatisticsPage from './pages/statistics/StatisticsPage.jsx'
import AddCategoryPage from './pages/catalog/AddCategoryPage.jsx'
import AddSpecificationPage from './pages/catalog/AddSpecificationPage.jsx'
import {useEffect, useState} from 'react'
import {Routes, Route, Navigate} from 'react-router-dom'

function App() {
  const getStoredUser = () => {
    try {
      const storedUser = JSON.parse(localStorage.getItem("loggedInUser") || "null")
      return storedUser?.userId && storedUser?.userType && storedUser?.token ? storedUser : null
    } catch {
      return null
    }
  }

  const [loggedInUser, setLoggedInUser] = useState(getStoredUser)

  useEffect(() => {
    if (!getStoredUser()) {
      localStorage.removeItem("loggedInUser")
    }
  }, [])

  const handleLogin = (user) => {
    setLoggedInUser(user)
    localStorage.setItem("loggedInUser", JSON.stringify(user))
  }

  const handleLogout = () => {
    setLoggedInUser(null)
    localStorage.removeItem("loggedInUser")
  }

  return (
      <>
        <Header loggedInUser={loggedInUser} onLogout={handleLogout}/>
        <Routes>
          <Route path="/" element={<Home loggedInUser={loggedInUser}/>}/>
          <Route path="/login" element={<Login onLogin={handleLogin}/>}/>
          <Route path="/register" element={<Register/>}/>
          <Route
              path="/my-rentals"
              element={loggedInUser?.userType === "CLIENT" ? <MyRentals loggedInUser={loggedInUser}/> : <Navigate to="/" replace/>}
          />
          <Route
              path="/add-camera"
              element={loggedInUser?.userType === "EMPLOYEE" ? <AddCameraPage loggedInUser={loggedInUser}/> : <Navigate to="/" replace/>}
          />
          <Route
              path="/add-category"
              element={loggedInUser?.userType === "EMPLOYEE" ? <AddCategoryPage loggedInUser={loggedInUser}/> : <Navigate to="/" replace/>}
          />
          <Route
              path="/add-specification"
              element={loggedInUser?.userType === "EMPLOYEE" ? <AddSpecificationPage loggedInUser={loggedInUser}/> : <Navigate to="/" replace/>}
          />
          <Route
              path="/clients"
              element={loggedInUser?.userType === "EMPLOYEE" ? <ClientsPage loggedInUser={loggedInUser}/> : <Navigate to="/" replace/>}
          />
          <Route
              path="/statistics"
              element={loggedInUser?.userType === "EMPLOYEE" ? <StatisticsPage loggedInUser={loggedInUser}/> : <Navigate to="/" replace/>}
          />
        </Routes>
      </>
  )
}

export default App
