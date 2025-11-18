import { useMemo, useState, useEffect } from 'react';
import './App.css';
import { IncidentHub } from '@stackspotlabs/incident-client';

interface IncidentFormProps {
    onCreate: (description: string) => Promise<void>;
}

function IncidentForm({ onCreate }: IncidentFormProps) {
    const [description, setDescription] = useState('');
    const [submitting, setSubmitting] = useState(false);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!description.trim()) return;
        setSubmitting(true);
        await onCreate(description);
        setDescription('');
        setSubmitting(false);
    };

    return (
        <form onSubmit={handleSubmit} style={{ marginBottom: '1em' }}>
            <input
                type="text"
                placeholder="Incident description"
                value={description}
                onChange={e => setDescription(e.target.value)}
                disabled={submitting}
            />
            <button type="submit" disabled={submitting || !description.trim()} style={{margin: '2px'}}>
                {submitting ? 'Sending...' : 'Send'}
            </button>
        </form>
    );
}

function App() {
    const incidentHub = useMemo(() => new IncidentHub({ endpoint: "http://localhost:9090" }), []);
    const [incidents, setIncidents] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const fetchIncidents = async () => {
        setLoading(true);
        setError(null);
        try {
            const result = await incidentHub.listIncidents();
            setIncidents(result.incidents as any || []);
        } catch (err) {
            setError('Failed to fetch incidents');
        }
        setLoading(false);
    };

    useEffect(() => {
        fetchIncidents();
        // eslint-disable-next-line
    }, []);

    const handleCreateIncident = async (description: string) => {
        try {
            await incidentHub.createIncident({ description });
            fetchIncidents();
        } catch (err) {
            setError('Failed to create incident');
        }
    };

    return (
        <>
            <header><h1>Incident App</h1></header>
            <main>
                <section className="form-section">
                    <h2>Report Incident</h2>
                    <IncidentForm onCreate={handleCreateIncident} />
                </section>
                <section className="list-section">
                    {loading && <p>Loading incidents...</p>}
                    {error && <p style={{ color: 'red' }}>{error}</p>}
                    {incidents.length === 0 ? (
                        <p>No incidents found</p>
                    ) : (
                        <ul className="incident-list">
                            {incidents.map((incident) => (
                                <li className="incident-card" key={incident.id}>
                                    <strong>{incident.id}</strong>: {incident.description} ({incident.status})
                                </li>
                            ))}
                        </ul>
                    )}
                </section>
            </main>
        </>
    );
}

export default App;