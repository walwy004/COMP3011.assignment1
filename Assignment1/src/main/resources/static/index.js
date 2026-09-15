const recordBtn = document.getElementById('recordBtn');
const statusText = document.getElementById('statusText');
const transcription = document.getElementById('transcription');
const icon = recordBtn.querySelector('i');

let isRecording = false;
let mediaRecorder;
let audioChunks = [];
let audioStream;

async function startRecording() {
	
	try {
		audioStream = await navigator.mediaDevices.getUserMedia({audio: true});
	}
	catch(err) {
		statusText.textContent = 'Couldn\'t access microphone without permissions';
		return;
	}
		
	mediaRecorder = new MediaRecorder(audioStream);
	audioChunks = [];	// Clear last recording
	
	mediaRecorder.addEventListener('dataavailable', event => {
		audioChunks.push(event.data);
	});
	
	mediaRecorder.addEventListener('stop', async () => {
		const audioBlob = new Blob(audioChunks, {
			type: mediaRecorder.mimeType
		});
		
		console.log("Audio type:", audioBlob.type);
		console.log("Audio size:", audioBlob.size);
		
		audioStream.getTracks().forEach(track => track.stop());
		
		await uploadAudio(audioBlob);
	})
	
	mediaRecorder.start();
	isRecording = true;
	
	recordBtn.classList.add('isRecording');
	icon.className = 'fa-solid fa-stop';	// Stop icon
	statusText.textContent = 'Recording…';
}


async function stopRecording() {
	
	mediaRecorder.stop();
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


async function uploadAudio(audioBlob) {
	
	const formData = new FormData();
	
	formData.append(
		'audio',
		audioBlob,
		'recording.webm'
	);
	
	const response = await fetch('/api/v1/transcriptions', {
		method: 'POST',
		body: formData
	});
	
	const data = await response.json();
	
	// Test for Titan AI
	if (!response.ok) {
	    console.error("Transcription failed:", data);
	    transcription.value = "Transcription failed.";
	    return;
	}
	
	transcription.value = data.text;
}


recordBtn.addEventListener('click', async () => {
	if (!isRecording) {
		await startRecording();
	} else {
		stopRecording();
	}
})
