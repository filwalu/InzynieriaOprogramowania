import { useEffect, useState } from 'react'
import { api } from '../api'

interface Ticket {
  id: number
  title: string
  description: string
  status: string
  priority: string
  createdById: number
  assignedToId?: number | null
}

interface User {
  id: number
  username: string
}

export default function TicketListPage() {
  const [tickets, setTickets] = useState<Ticket[]>([])
  const [users, setUsers] = useState<User[]>([])
  const [loading, setLoading] = useState(true)
  const [showModal, setShowModal] = useState(false)

  const [newTitle, setNewTitle] = useState('')
  const [newDescription, setNewDescription] = useState('')
  const [newPriority, setNewPriority] = useState('MEDIUM')
  const [newAssignedTo, setNewAssignedTo] = useState<number | null>(null)

  useEffect(() => {
    loadData()
  }, [])

  const loadData = async () => {
    setLoading(true)
    try {
      const [ticketsRes, usersRes] = await Promise.all([
        api.get('/tickets'),
        api.get('/admin/users'),
      ])
      setTickets(ticketsRes.data)
      setUsers(usersRes.data)
    } catch (err) {
      console.error('Load failed', err)
    } finally {
      setLoading(false)
    }
  }

  const createTicket = async () => {
    if (!newTitle.trim() || !newDescription.trim()) {
      alert('Title and description required')
      return
    }
    try {
      await api.post('/tickets', {
        title: newTitle,
        description: newDescription,
        priority: newPriority,
        status: 'OPEN',
        createdById: 1,
        assignedToId: newAssignedTo,
      })
      setShowModal(false)
      setNewTitle('')
      setNewDescription('')
      setNewPriority('MEDIUM')
      setNewAssignedTo(null)
      loadData()
    } catch (err) {
      console.error('Create failed', err)
    }
  }

  const deleteTicket = async (id: number) => {
    if (!confirm('Delete this ticket?')) return
    await api.delete(`/tickets/${id}`)
    loadData()
  }

  const updateStatus = async (id: number, status: string) => {
    await api.post(`/tickets/${id}/status?status=${status}`)
    loadData()
  }

  const updatePriority = async (id: number, priority: string) => {
    await api.post(`/tickets/${id}/priority?priority=${priority}`)
    loadData()
  }

  const assignTicket = async (id: number, userId: number) => {
    await api.post(`/tickets/${id}/assign/${userId}`)
    loadData()
  }

  const getStatusBadge = (status: string) => {
    const colors: any = {
      OPEN: 'bg-blue-100 text-blue-800',
      IN_PROGRESS: 'bg-yellow-100 text-yellow-800',
      RESOLVED: 'bg-green-100 text-green-800',
      CLOSED: 'bg-gray-100 text-gray-800',
    }
    return colors[status] || 'bg-gray-100 text-gray-800'
  }

  const getPriorityBadge = (priority: string) => {
    const colors: any = {
      LOW: 'bg-green-100 text-green-800',
      MEDIUM: 'bg-yellow-100 text-yellow-800',
      HIGH: 'bg-orange-100 text-orange-800',
      CRITICAL: 'bg-red-100 text-red-800',
    }
    return colors[priority] || 'bg-gray-100 text-gray-800'
  }

  return (
    <div className="max-w-7xl mx-auto px-4 py-8">
      {/* Header */}
      <div className="flex justify-between items-center mb-6">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">Tickets</h1>
          <p className="text-gray-500">Manage your support tickets</p>
        </div>
        <button
          onClick={() => setShowModal(true)}
          className="bg-indigo-600 hover:bg-indigo-700 text-white px-4 py-2 rounded-md shadow font-medium transition"
        >
          + New Ticket
        </button>
      </div>

      {/* Table */}
      <div className="bg-white shadow rounded-lg overflow-x-auto">
        <table className="min-w-full">
          <thead className="bg-gradient-to-r from-indigo-500 to-purple-600">
            <tr>
              <th className="px-6 py-3 text-left text-xs font-medium text-white uppercase">ID</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-white uppercase">Title</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-white uppercase">Status</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-white uppercase">Priority</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-white uppercase">Assigned To</th>
              <th className="px-6 py-3 text-right text-xs font-medium text-white uppercase">Actions</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-gray-200">
            {tickets.map(ticket => (
              <tr key={ticket.id} className="hover:bg-gray-50">
                <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-800">#{ticket.id}</td>
                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{ticket.title}</td>
                <td className="px-6 py-4 whitespace-nowrap">
                  <select
                    value={ticket.status}
                    onChange={e => updateStatus(ticket.id, e.target.value)}
                    className={`text-xs font-medium rounded-full px-3 py-1 border-0 ${getStatusBadge(ticket.status)}`}
                  >
                    <option value="OPEN">Open</option>
                    <option value="IN_PROGRESS">In Progress</option>
                    <option value="RESOLVED">Resolved</option>
                    <option value="CLOSED">Closed</option>
                  </select>
                </td>
                <td className="px-6 py-4 whitespace-nowrap">
                  <select
                    value={ticket.priority}
                    onChange={e => updatePriority(ticket.id, e.target.value)}
                    className={`text-xs font-medium rounded-full px-3 py-1 border-0 ${getPriorityBadge(ticket.priority)}`}
                  >
                    <option value="LOW">Low</option>
                    <option value="MEDIUM">Medium</option>
                    <option value="HIGH">High</option>
                    <option value="CRITICAL">Critical</option>
                  </select>
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-700">
                  <select
                    value={ticket.assignedToId || ''}
                    onChange={e => assignTicket(ticket.id, parseInt(e.target.value))}
                    className="text-sm border-gray-300 rounded px-2 py-1"
                  >
                    <option value="">Unassigned</option>
                    {users.map(user => (
                      <option key={user.id} value={user.id}>{user.username}</option>
                    ))}
                  </select>
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-right text-sm">
                  <button
                    onClick={() => deleteTicket(ticket.id)}
                    className="text-red-500 hover:text-red-700 font-medium"
                  >
                    Delete
                  </button>
                </td>
              </tr>
            ))}
            {!loading && tickets.length === 0 && (
              <tr>
                <td colSpan={6} className="text-center py-6 text-gray-500">No tickets found.</td>
              </tr>
            )}
          </tbody>
        </table>
        {loading && (
          <div className="p-6 text-center text-gray-500">Loading...</div>
        )}
      </div>

      {/* Modal */}
      {showModal && (
        <div className="fixed inset-0 bg-black bg-opacity-40 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg shadow-lg p-6 w-full max-w-md relative">
            <h2 className="text-xl font-bold mb-4 text-gray-900">Create Ticket</h2>
            <div className="space-y-4">
              <input
                className="w-full border border-gray-300 rounded px-4 py-2"
                placeholder="Title"
                value={newTitle}
                onChange={e => setNewTitle(e.target.value)}
              />
              <textarea
                className="w-full border border-gray-300 rounded px-4 py-2"
                placeholder="Description"
                rows={4}
                value={newDescription}
                onChange={e => setNewDescription(e.target.value)}
              />
              <select
                className="w-full border border-gray-300 rounded px-4 py-2"
                value={newPriority}
                onChange={e => setNewPriority(e.target.value)}
              >
                <option value="LOW">Low</option>
                <option value="MEDIUM">Medium</option>
                <option value="HIGH">High</option>
                <option value="CRITICAL">Critical</option>
              </select>
              <select
                className="w-full border border-gray-300 rounded px-4 py-2"
                value={newAssignedTo || ''}
                onChange={e => setNewAssignedTo(e.target.value ? parseInt(e.target.value) : null)}
              >
                <option value="">Unassigned</option>
                {users.map(user => (
                  <option key={user.id} value={user.id}>{user.username}</option>
                ))}
              </select>
            </div>
            <div className="flex justify-end gap-3 mt-6">
              <button
                onClick={() => setShowModal(false)}
                className="px-4 py-2 text-gray-500 hover:text-gray-700"
              >
                Cancel
              </button>
              <button
                onClick={createTicket}
                className="bg-indigo-600 hover:bg-indigo-700 text-white px-6 py-2 rounded-md"
              >
                Create
              </button>
            </div>
            <button
              onClick={() => setShowModal(false)}
              className="absolute top-3 right-3 text-gray-400 hover:text-gray-600"
            >
              ✕
            </button>
          </div>
        </div>
      )}
    </div>
  )
}
