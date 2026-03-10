const API_BASE = '/api';

async function apiFetch(path, options = {}) {
  const response = await fetch(`${API_BASE}${path}`, {
    headers: { 'Content-Type': 'application/json' },
    ...options
  });

  if (!response.ok) {
    const errorText = await response.text();
    throw new Error(errorText || `HTTP ${response.status}`);
  }

  const contentType = response.headers.get('content-type') || '';
  return contentType.includes('application/json') ? response.json() : response.text();
}

function setActiveNav() {
  const page = location.pathname.split('/').pop() || 'index.html';
  document.querySelectorAll('.nav a').forEach((a) => {
    a.classList.toggle('active', a.getAttribute('href') === page);
  });
}

function urgencyBadge(value) {
  const urgency = String(value).toLowerCase();
  if (urgency === 'high' || urgency === '5' || urgency === '4') {
    return '<span class="badge high">High</span>';
  }
  if (urgency === 'medium' || urgency === '3') {
    return '<span class="badge medium">Medium</span>';
  }
  return '<span class="badge low">Low</span>';
}

async function loadDashboard() {
  const [requests, centers, logs] = await Promise.all([
    apiFetch('/requests').catch(() => []),
    apiFetch('/centers').catch(() => []),
    apiFetch('/logs').catch(() => [])
  ]);

  const totalRequests = requests.length;
  const activeRequests = requests.filter((r) => !r.allocated).length;
  document.getElementById('totalRequests').textContent = totalRequests;
  document.getElementById('activeRequests').textContent = activeRequests;
  document.getElementById('centerCount').textContent = centers.length;

  const recentLogs = logs.slice(-6).reverse();
  const tbody = document.getElementById('recentLogsBody');
  tbody.innerHTML = recentLogs.length
    ? recentLogs.map((l) => `<tr><td>${l.timestamp || '-'}</td><td>${l.message || l}</td></tr>`).join('')
    : '<tr><td colspan="2">No recent logs available.</td></tr>';
}

function initRequestForm() {
  const form = document.getElementById('requestForm');
  const message = document.getElementById('requestMessage');

  form.addEventListener('submit', async (e) => {
    e.preventDefault();

    const payload = {
      location: document.getElementById('location').value.trim(),
      urgency: document.getElementById('urgency').value,
      foodNeeded: Number(document.getElementById('foodNeeded').value),
      waterNeeded: Number(document.getElementById('waterNeeded').value),
      medicineNeeded: Number(document.getElementById('medicineNeeded').value)
    };

    if (!payload.location) {
      message.className = 'message error';
      message.textContent = 'Location is required.';
      return;
    }

    try {
      await apiFetch('/requests', { method: 'POST', body: JSON.stringify(payload) });
      message.className = 'message success';
      message.textContent = '✅ Request submitted successfully.';
      form.reset();
    } catch (error) {
      message.className = 'message error';
      message.textContent = `❌ ${error.message}`;
    }
  });
}

async function loadCenters() {
  const centers = await apiFetch('/centers').catch(() => []);
  const tbody = document.getElementById('centersBody');

  tbody.innerHTML = centers.length
    ? centers.map((c) => `
      <tr>
        <td>${c.name}</td>
        <td>${c.location}</td>
        <td>${c.foodStock}</td>
        <td>${c.waterStock}</td>
        <td>${c.medicineStock}</td>
      </tr>`).join('')
    : '<tr><td colspan="5">No centers available.</td></tr>';
}

function initAllocation() {
  const runBtn = document.getElementById('runAllocationBtn');
  const output = document.getElementById('allocationResult');

  runBtn.addEventListener('click', async () => {
    output.textContent = 'Running allocation...';

    try {
      const result = await apiFetch('/allocate', { method: 'POST' });
      output.innerHTML = `
        <strong>Status:</strong> ${result.status || 'SUCCESS'}<br>
        <strong>Center:</strong> ${result.centerName || '-'}<br>
        <strong>Request:</strong> ${result.requestId || '-'}<br>
        <strong>Shortest Distance:</strong> ${result.distance ?? '-'} km<br>
        <strong>Remaining Stock:</strong> Food ${result.remainingFood ?? '-'}, Water ${result.remainingWater ?? '-'}, Medicine ${result.remainingMedicine ?? '-'}
      `;
    } catch (error) {
      output.textContent = `Allocation failed: ${error.message}`;
    }
  });
}

async function loadLogs() {
  const logs = await apiFetch('/logs').catch(() => []);
  const tbody = document.getElementById('logsBody');

  tbody.innerHTML = logs.length
    ? logs.map((l) => `<tr><td>${l.timestamp || '-'}</td><td>${l.level || 'INFO'}</td><td>${l.message || l}</td></tr>`).join('')
    : '<tr><td colspan="3">No logs found.</td></tr>';
}

function initPage() {
  setActiveNav();
  const page = document.body.dataset.page;

  if (page === 'dashboard') loadDashboard();
  if (page === 'requests') initRequestForm();
  if (page === 'centers') loadCenters();
  if (page === 'allocation') initAllocation();
  if (page === 'logs') loadLogs();
}

document.addEventListener('DOMContentLoaded', initPage);

// Example API calls (reference):
// fetch('/api/requests', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ location:'North Zone', urgency:'High', foodNeeded:20, waterNeeded:30, medicineNeeded:10 }) })
// fetch('/api/centers').then(r => r.json()).then(console.log)
// fetch('/api/allocate', { method: 'POST' }).then(r => r.json()).then(console.log)
// fetch('/api/logs').then(r => r.json()).then(console.log)
