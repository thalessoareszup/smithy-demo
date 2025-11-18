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
            <button type="submit" disabled={submitting || !description.trim()}>
                {submitting ? 'Creating...' : 'Create Incident'}
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
            <h1>Incident App</h1>
            <IncidentForm onCreate={handleCreateIncident} />
            {loading && <p>Loading incidents...</p>}
            {error && <p style={{ color: 'red' }}>{error}</p>}
            <ul>
                {incidents.map((incident: any) => (
                    <li key={incident.id}>
                        <strong>{incident.id}</strong>: {incident.description} ({incident.status})
                    </li>
                ))}
            </ul>
        </>
    );
}

export default App;