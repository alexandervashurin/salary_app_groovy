function toggleAddForm() {
    const form = document.getElementById('addWorkerForm');
    form.style.display = form.style.display === 'none' ? 'block' : 'none';
}

document.getElementById('newWorkerForm')?.addEventListener('submit', async function(e) {
    e.preventDefault();
    
    const formData = new FormData(this);
    
    try {
        const response = await fetch('/api/add-worker', {
            method: 'POST',
            body: formData
        });
        
        const result = await response.json();
        
        if (result.success) {
            alert('Работник успешно добавлен!');
            this.reset();
            toggleAddForm();
            location.reload();
        } else {
            alert('Ошибка при добавлении работника');
        }
    } catch (error) {
        console.error('Error:', error);
        alert('Произошла ошибка при добавлении работника');
    }
});