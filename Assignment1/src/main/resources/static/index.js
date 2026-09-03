const recordBtn = document.getElementById('recordBtn');
const icon = recordBtn.querySelector('i');
let isRecording = false;

function startRecording() {
	isRecording = true;
	recordBtn.classList.add('isRecording');
	icon.className = 'fa-solid fa-stop';	// Stop icon
	
}

function stopRecording() {
	isRecording = false;
	recordBtn.classList.remove('isRecording');
	icon.className = 'fa-solid fa-record-vinyl';	// Record icon
}

recordBtn.addEventListener('click', async () => {
	if (!isRecording) {
		startRecording();
	} else {
		stopRecording();
	}
})
