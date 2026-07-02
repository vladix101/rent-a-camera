import './App.css'
import Header from './pages/header/Header.jsx'
import Home from './pages/home/Home.jsx'
import Login from './pages/login/Login.jsx'
import {useEffect, useState} from 'react'
import {Routes, Route} from 'react-router-dom'

function App() {
  const getStoredUser = () => {
    try {
      const storedUser = JSON.parse(localStorage.getItem("loggedInUser") || "null")
      return storedUser?.userId && storedUser?.userType ? storedUser : null
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
          <Route path="/" element={<Home/>}/>
          <Route path="/login" element={<Login onLogin={handleLogin}/>}/>
        </Routes>
      </>
  )
}

export default App
