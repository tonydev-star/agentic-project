import React, { useState, useEffect, useCallback } from 'react';
import { useParams, Link } from 'react-router-dom';
import { ArrowLeftIcon, PencilIcon, CheckCircleIcon } from '@heroicons/react/24/outline';
import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api/v1';

const FeedbackDetail = () => {
  const { id } = useParams();
  const [feedback, setFeedback] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [editingResponse, setEditingResponse] = useState(false);
  const [humanResponse, setHumanResponse] = useState('');
  const [saving, setSaving] = useState(false);

  const fetchFeedback = useCallback(async () => {
    try {
      setLoading(true);
      const response = await axios.get(`${API_BASE_URL}/feedback/${id}`);
      setFeedback(response.data);
      setHumanResponse(response.data.humanResponse || '');
    } catch (err) {
      setError('Failed to fetch feedback details');
      console.error('Error fetching feedback:', err);
    } finally {
      setLoading(false);
    }
  }, [id]);

  useEffect(() => {
    fetchFeedback();
  }, [fetchFeedback]);

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

  const handleMarkAsReviewed = async () => {
    try {
      setSaving(true);
      await axios.post(`${API_BASE_URL}/feedback/${id}/human-review`, {
        response: humanResponse
      });
      
      // Refresh the feedback data
      await fetchFeedback();
      setEditingResponse(false);
    } catch (err) {
      console.error('Error marking as reviewed:', err);
      setError('Failed to save review');
    } finally {
      setSaving(false);
    }
  };

  const handleOverrideResponse = async () => {
    try {
      setSaving(true);
      await axios.post(`${API_BASE_URL}/feedback/${id}/override-response`, {
        newResponse: humanResponse
      });
      
      // Refresh the feedback data
      await fetchFeedback();
      setEditingResponse(false);
    } catch (err) {
      console.error('Error overriding response:', err);
      setError('Failed to override response');
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto"></div>
          <p className="mt-4 text-gray-600">Loading feedback details...</p>
        </div>
      </div>
    );
  }

  if (error || !feedback) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <p className="text-red-600 text-lg">{error || 'Feedback not found'}</p>
          <Link
            to="/"
            className="mt-4 inline-block px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
          >
            Back to Dashboard
          </Link>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <header className="bg-white shadow-sm border-b">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex items-center py-6">
            <Link
              to="/"
              className="flex items-center text-gray-600 hover:text-gray-900"
            >
              <ArrowLeftIcon className="h-5 w-5 mr-2" />
              Back to Dashboard
            </Link>
          </div>
        </div>
      </header>

      <main className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Feedback Card */}
        <div className="bg-white rounded-lg shadow">
          <div className="p-6">
            {/* Header */}
            <div className="flex items-start justify-between mb-6">
              <div className="flex-1">
                <div className="flex items-center mb-4">
                  <h1 className="text-2xl font-bold text-gray-900 mr-4">Feedback Details</h1>
                  {feedback.humanReviewed && (
                    <span className="flex items-center px-3 py-1 bg-green-100 text-green-800 rounded-full text-sm">
                      <CheckCircleIcon className="h-4 w-4 mr-1" />
                      Reviewed
                    </span>
                  )}
                </div>
                
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-6">
                  <div>
                    <p className="text-sm text-gray-600">Author</p>
                    <p className="font-medium text-gray-900">{feedback.author}</p>
                  </div>
                  <div>
                    <p className="text-sm text-gray-600">Source</p>
                    <p className="font-medium text-gray-900">{feedback.source}</p>
                  </div>
                  <div>
                    <p className="text-sm text-gray-600">Sentiment</p>
                    <span className={`inline-block px-3 py-1 rounded-full text-sm font-medium ${getSentimentBgColor(feedback.sentiment)} ${getSentimentColor(feedback.sentiment)}`}>
                      {feedback.sentiment}
                    </span>
                  </div>
                  <div>
                    <p className="text-sm text-gray-600">Confidence</p>
                    <p className="font-medium text-gray-900">{(feedback.sentimentConfidence * 100).toFixed(1)}%</p>
                  </div>
                  <div>
                    <p className="text-sm text-gray-600">Original Date</p>
                    <p className="font-medium text-gray-900">
                      {new Date(feedback.createdAtSource).toLocaleDateString()} {new Date(feedback.createdAtSource).toLocaleTimeString()}
                    </p>
                  </div>
                  <div>
                    <p className="text-sm text-gray-600">Scraped Date</p>
                    <p className="font-medium text-gray-900">
                      {new Date(feedback.scrapedAt).toLocaleDateString()} {new Date(feedback.scrapedAt).toLocaleTimeString()}
                    </p>
                  </div>
                </div>
              </div>
            </div>

            {/* Feedback Content */}
            <div className="mb-8">
              <h2 className="text-lg font-semibold text-gray-900 mb-3">Feedback Content</h2>
              <div className="bg-gray-50 rounded-lg p-4">
                <p className="text-gray-700 whitespace-pre-wrap">{feedback.content}</p>
              </div>
            </div>

            {/* Automated Response */}
            {feedback.autoResponse && (
              <div className="mb-8">
                <h2 className="text-lg font-semibold text-gray-900 mb-3">Automated Response</h2>
                <div className="bg-blue-50 rounded-lg p-4">
                  <p className="text-gray-700 whitespace-pre-wrap">{feedback.autoResponse}</p>
                  {feedback.responseSentAt && (
                    <p className="text-sm text-gray-500 mt-2">
                      Sent: {new Date(feedback.responseSentAt).toLocaleDateString()} {new Date(feedback.responseSentAt).toLocaleTimeString()}
                    </p>
                  )}
                </div>
              </div>
            )}

            {/* Human Review Section */}
            <div className="border-t pt-6">
              <div className="flex items-center justify-between mb-4">
                <h2 className="text-lg font-semibold text-gray-900">Human Review</h2>
                {!editingResponse && (
                  <button
                    onClick={() => setEditingResponse(true)}
                    className="flex items-center px-3 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700"
                  >
                    <PencilIcon className="h-4 w-4 mr-2" />
                    {feedback.humanReviewed ? 'Edit Response' : 'Add Response'}
                  </button>
                )}
              </div>

              {editingResponse ? (
                <div className="space-y-4">
                  <textarea
                    value={humanResponse}
                    onChange={(e) => setHumanResponse(e.target.value)}
                    placeholder="Enter your response or review notes..."
                    className="w-full h-32 px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                  />
                  <div className="flex space-x-3">
                    <button
                      onClick={feedback.humanReviewed ? handleOverrideResponse : handleMarkAsReviewed}
                      disabled={saving}
                      className="px-4 py-2 bg-green-600 text-white rounded-md hover:bg-green-700 disabled:opacity-50"
                    >
                      {saving ? 'Saving...' : feedback.humanReviewed ? 'Override Response' : 'Mark as Reviewed'}
                    </button>
                    <button
                      onClick={() => {
                        setEditingResponse(false);
                        setHumanResponse(feedback.humanResponse || '');
                      }}
                      className="px-4 py-2 bg-gray-300 text-gray-700 rounded-md hover:bg-gray-400"
                    >
                      Cancel
                    </button>
                  </div>
                </div>
              ) : feedback.humanResponse ? (
                <div className="bg-green-50 rounded-lg p-4">
                  <p className="text-gray-700 whitespace-pre-wrap">{feedback.humanResponse}</p>
                </div>
              ) : (
                <div className="text-center py-8 bg-gray-50 rounded-lg">
                  <p className="text-gray-500">No human review yet</p>
                </div>
              )}
            </div>
          </div>
        </div>
      </main>
    </div>
  );
};

export default FeedbackDetail;
