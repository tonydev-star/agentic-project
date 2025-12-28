import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { ChartBarIcon, ChatBubbleLeftRightIcon, FaceSmileIcon, FaceFrownIcon } from '@heroicons/react/24/outline';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, PieChart, Pie, Cell } from 'recharts';
import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api/v1';

const Dashboard = () => {
  const [stats, setStats] = useState(null);
  const [feedback, setFeedback] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      setLoading(true);
      const [statsResponse, feedbackResponse] = await Promise.all([
        axios.get(`${API_BASE_URL}/feedback/stats`),
        axios.get(`${API_BASE_URL}/feedback`)
      ]);

      setStats(statsResponse.data);
      setFeedback(feedbackResponse.data);
    } catch (err) {
      setError('Failed to fetch data from server');
      console.error('Error fetching data:', err);
    } finally {
      setLoading(false);
    }
  };

  const getSentimentColor = (sentiment) => {
    switch (sentiment) {
      case 'POSITIVE': return 'text-green-600';
      case 'NEGATIVE': return 'text-red-600';
      case 'NEUTRAL': return 'text-gray-600';
      default: return 'text-gray-600';
    }
  };

  const getSentimentBgColor = (sentiment) => {
    switch (sentiment) {
      case 'POSITIVE': return 'bg-green-100';
      case 'NEGATIVE': return 'bg-red-100';
      case 'NEUTRAL': return 'bg-gray-100';
      default: return 'bg-gray-100';
    }
  };

  const pieData = stats ? [
    { name: 'Positive', value: stats.positive, color: '#10b981' },
    { name: 'Negative', value: stats.negative, color: '#ef4444' },
    { name: 'Neutral', value: stats.neutral, color: '#6b7280' }
  ] : [];

  const barData = stats ? [
    { name: 'Positive', count: stats.positive },
    { name: 'Negative', count: stats.negative },
    { name: 'Neutral', count: stats.neutral }
  ] : [];

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto"></div>
          <p className="mt-4 text-gray-600">Loading dashboard...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <p className="text-red-600 text-lg">{error}</p>
          <button
            onClick={fetchData}
            className="mt-4 px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
          >
            Retry
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <header className="bg-white shadow-sm border-b">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center py-6">
            <div className="flex items-center">
              <ChatBubbleLeftRightIcon className="h-8 w-8 text-blue-600 mr-3" />
              <h1 className="text-2xl font-bold text-gray-900">Customer Feedback Dashboard</h1>
            </div>
            <button
              onClick={fetchData}
              className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition-colors"
            >
              Refresh
            </button>
          </div>
        </div>
      </header>

      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Stats Cards */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
          <div className="bg-white rounded-lg shadow p-6">
            <div className="flex items-center">
              <div className="flex-shrink-0">
                <ChartBarIcon className="h-8 w-8 text-blue-600" />
              </div>
              <div className="ml-4">
                <p className="text-sm font-medium text-gray-600">Total Feedback</p>
                <p className="text-2xl font-bold text-gray-900">{stats?.total || 0}</p>
              </div>
            </div>
          </div>

          <div className="bg-white rounded-lg shadow p-6">
            <div className="flex items-center">
              <div className="flex-shrink-0">
                <FaceSmileIcon className="h-8 w-8 text-green-600" />
              </div>
              <div className="ml-4">
                <p className="text-sm font-medium text-gray-600">Positive</p>
                <p className="text-2xl font-bold text-green-600">{stats?.positive || 0}</p>
                <p className="text-xs text-gray-500">{stats?.positivePercentage?.toFixed(1) || 0}%</p>
              </div>
            </div>
          </div>

          <div className="bg-white rounded-lg shadow p-6">
            <div className="flex items-center">
              <div className="flex-shrink-0">
                <FaceFrownIcon className="h-8 w-8 text-red-600" />
              </div>
              <div className="ml-4">
                <p className="text-sm font-medium text-gray-600">Negative</p>
                <p className="text-2xl font-bold text-red-600">{stats?.negative || 0}</p>
                <p className="text-xs text-gray-500">{stats?.negativePercentage?.toFixed(1) || 0}%</p>
              </div>
            </div>
          </div>

          <div className="bg-white rounded-lg shadow p-6">
            <div className="flex items-center">
              <div className="flex-shrink-0">
                <div className="h-8 w-8 bg-yellow-100 rounded-full flex items-center justify-center">
                  <span className="text-yellow-600 font-semibold">!</span>
                </div>
              </div>
              <div className="ml-4">
                <p className="text-sm font-medium text-gray-600">Unreviewed</p>
                <p className="text-2xl font-bold text-yellow-600">{stats?.unreviewed || 0}</p>
              </div>
            </div>
          </div>
        </div>

        {/* Charts */}
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-8">
          <div className="bg-white rounded-lg shadow p-6">
            <h2 className="text-lg font-semibold text-gray-900 mb-4">Sentiment Distribution</h2>
            <div className="space-y-4">
              {pieData.map((item, index) => (
                <div key={index} className="flex items-center justify-between">
                  <div className="flex items-center">
                    <div 
                      className="w-4 h-4 rounded mr-3" 
                      style={{ backgroundColor: item.color }}
                    ></div>
                    <span className="text-sm font-medium">{item.name}</span>
                  </div>
                  <div className="flex items-center">
                    <div className="w-32 bg-gray-200 rounded-full h-2 mr-3">
                      <div 
                        className="h-2 rounded-full" 
                        style={{ 
                          width: `${stats ? (item.value / stats.total * 100) : 0}%`,
                          backgroundColor: item.color 
                        }}
                      ></div>
                    </div>
                    <span className="text-sm text-gray-600">
                      {stats ? Math.round(item.value / stats.total * 100) : 0}%
                    </span>
                  </div>
                </div>
              ))}
            </div>
          </div>

          <div className="bg-white rounded-lg shadow p-6">
            <h2 className="text-lg font-semibold text-gray-900 mb-4">Sentiment Breakdown</h2>
            <div className="space-y-4">
              {barData.map((item, index) => (
                <div key={index} className="flex items-center justify-between">
                  <span className="text-sm font-medium w-20">{item.name}</span>
                  <div className="flex-1 mx-4">
                    <div className="bg-gray-200 rounded-full h-6">
                      <div 
                        className="bg-blue-500 h-6 rounded-full flex items-center justify-center text-white text-xs font-medium"
                        style={{ width: `${stats ? (item.count / stats.total * 100) : 0}%` }}
                      >
                        {item.count}
                      </div>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>

        {/* Recent Feedback */}
        <div className="bg-white rounded-lg shadow">
          <div className="px-6 py-4 border-b border-gray-200">
            <h2 className="text-lg font-semibold text-gray-900">Recent Feedback</h2>
          </div>
          <div className="divide-y divide-gray-200">
            {feedback.slice(0, 10).map((item) => (
              <div key={item.id} className="p-6 hover:bg-gray-50">
                <div className="flex items-start justify-between">
                  <div className="flex-1">
                    <div className="flex items-center mb-2">
                      <span className="text-sm font-medium text-gray-900">{item.author}</span>
                      <span className={`ml-2 px-2 py-1 text-xs rounded-full ${getSentimentBgColor(item.sentiment)} ${getSentimentColor(item.sentiment)}`}>
                        {item.sentiment}
                      </span>
                      <span className="ml-2 text-xs text-gray-500">{item.source}</span>
                    </div>
                    <p className="text-gray-700 mb-2">{item.content}</p>
                    <div className="flex items-center text-xs text-gray-500">
                      <span>{new Date(item.createdAtSource).toLocaleDateString()}</span>
                      {item.autoResponse && (
                        <span className="ml-4 text-green-600">• Auto-response sent</span>
                      )}
                    </div>
                  </div>
                  <Link
                    to={`/feedback/${item.id}`}
                    className="ml-4 px-3 py-1 text-sm bg-blue-100 text-blue-700 rounded hover:bg-blue-200"
                  >
                    View Details
                  </Link>
                </div>
              </div>
            ))}
          </div>
        </div>
      </main>
    </div>
  );
};

export default Dashboard;
