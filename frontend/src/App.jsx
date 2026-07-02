import './App.css'
import Header from './pages/header/Header.jsx'
import Home from './pages/home/Home.jsx'
import Login from './pages/login/Login.jsx'
import Register from './pages/register/Register.jsx'
import MyRentals from './pages/myRentals/MyRentals.jsx'
import AddCameraPage from './pages/camera/AddCameraPage.jsx'
import ClientsPage from './pages/clients/ClientsPage.jsx'
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
              path="/moja-iznajmljivanja"
              element={loggedInUser?.userType === "KLIJENT" ? <MyRentals loggedInUser={loggedInUser}/> : <Navigate to="/" replace/>}
          />
          <Route
              path="/dodaj-aparat"
              element={loggedInUser?.userType === "ZAPOSLENI" ? <AddCameraPage loggedInUser={loggedInUser}/> : <Navigate to="/" replace/>}
          />
          <Route
              path="/klijenti"
              element={loggedInUser?.userType === "ZAPOSLENI" ? <ClientsPage loggedInUser={loggedInUser}/> : <Navigate to="/" replace/>}
          />
        </Routes>
      </>
  )
}

export default App
