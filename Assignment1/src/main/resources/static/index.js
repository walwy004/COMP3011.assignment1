const recordBtn = document.getElementById('recordBtn');
const statusText = document.getElementById('statusText');
const icon = recordBtn.querySelector('i');
let isRecording = false;

function startRecording() {
	isRecording = true;
	recordBtn.classList.add('isRecording');
	icon.className = 'fa-solid fa-stop';	// Stop icon
	statusText.textContent = 'Recording…';
}

function stopRecording() {
	isRecording = false;
	recordBtn.classList.remove('isRecording');
	recordBtn.classList.add('isStopped');
	statusText.textContent = 'Stopped';
	
	setTimeout(() => { 
		recordBtn.classList.remove('isStopped');
		icon.className = 'fa-solid fa-record-vinyl';	// Record icon
	}, 400);
	
	setTimeout(() => { statusText.textContent = 'Ready'; }, 1000);		// reset after a moment
}

recordBtn.addEventListener('click', async () => {
	if (!isRecording) {
		startRecording();
	} else {
		stopRecording();
	}
})
