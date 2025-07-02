import { useState, useEffect } from 'react'
import LoginPage from './pages/LoginPage'
import TicketListPage from './pages/TicketListPage'
import './App.css'

function App() {
  const [loggedIn, setLoggedIn] = useState(!!localStorage.getItem('token'))
  const [username, setUsername] = useState('')

  useEffect(() => {
    const storedUsername = localStorage.getItem('username')
    if (storedUsername) {
      setUsername(storedUsername)
    }
  }, [loggedIn])

  const handleLogin = (user: string) => {
    setLoggedIn(true)
    setUsername(user)
    localStorage.setItem('username', user)
  }

  const handleLogout = () => {
    setLoggedIn(false)
    setUsername('')
    localStorage.removeItem('token')
    localStorage.removeItem('username')
  }

  if (!loggedIn) {
    return <LoginPage onLogin={handleLogin} />
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <header className="bg-white shadow-sm border-b">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center h-16">
            <h1 className="text-xl font-semibold text-gray-900">
               Ticketing System
            </h1>
            <div className="flex items-center space-x-4">
              <span className="text-sm text-gray-600">
                Welcome, <span className="font-medium text-gray-900">{username}</span>
              </span>
              <button
                onClick={handleLogout}
                className="bg-red-500 hover:bg-red-600 text-white px-4 py-2 rounded-lg text-sm font-medium transition-colors"
              >
                Logout
              </button>
            </div>
          </div>
        </div>
      </header>
      <main>
        <TicketListPage />
      </main>
    </div>
  )
}

export default App